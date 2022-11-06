package com.collidacube.bot.data.report;

import java.awt.Color;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import com.collidacube.bot.Bot;
import com.collidacube.bot.log.LoggingCategory;
import com.collidacube.javacordext.data.DataManager;
import com.collidacube.javacordext.data.DataPackage;
import com.collidacube.javacordext.utils.specialized.Utils;

public class Report extends DataPackage<Report> {

	private static final ServerTextChannel messageReports = Bot.getTextChannelById(1030941156017709167L);
    private static final HashMap<String, Report> reportsByReceiptId = new HashMap<>();

    public static Report getReportByReceiptId(String receiptId) {
        return reportsByReceiptId.get(receiptId);
    }

	public static final DataManager<Report> DATA_MANAGER = new DataManager<>(Report.class, Report::loadFrom, Bot.bot.dataPath + "Reports.db",
																		"receiptMessageId",
																		"reportedMessageLink",
																		"comments",
																		"reporterId");

	private static Report loadFrom(HashMap<String, String> data) {
        String receiptMessageId = data.get("receiptMessageId");
        String reportedMessageLink = data.get("reportedMessageLink");
        String comments = data.get("comments");
        String reporterId = data.get("reporterId");

        User reporter = Bot.api.getUserById(reporterId).join();

        CompletableFuture<Message> receiptMessage = messageReports.getMessageById(receiptMessageId);
        CompletableFuture<Message> cf_reportedMsg = Bot.api.getMessageByLink(reportedMessageLink).orElse(null);
        Message reportedMessage = Utils.await(cf_reportedMsg, false);
        if (reportedMessage == null) {
            receiptMessage.whenComplete((msg, err) -> msg.delete("Original message doesn't exist."));
            return null;
        }

        Report report = new Report(reportedMessage, comments, reporter);
        report.setReceiptMessage(receiptMessage);
        return report;
    }

    private final Message reportedMessage;
    private final User reportedUser;
    private Message receiptMessage = null;
    private final String comments;
    private final User reporter;
	public Report(Message reportedMessage, String comments, User reporter) {
        super(Report.class, DATA_MANAGER);
        this.reportedMessage = reportedMessage;
        this.reportedUser = reportedMessage.getUserAuthor().orElse(null);
        this.comments = comments;
        this.reporter = reporter;
    }

    public EmbedBuilder submitReport() {
        if (receiptMessage != null) return null;

        String msgLink = reportedMessage.getLink().toExternalForm();

        //String commentsBlock = Utils.isNull(comments) ? "" : String.format("**Comments**\n%1$s: `%2$s`\n\n", reporter.getMentionTag(), comments);
        //String description = String.format("**Reported Message**\n%1$s: `%2$s`\n\n%3$s%4$s", reportedUser.getMentionTag(), reportedMessage.getContent(), commentsBlock, LoggingCategory.getTime());
        String description = "**ReportedMessage**"
                + "\n" + reportedUser.getMentionTag() + ": `" + reportedMessage.getContent() + "`\n"
                + (Utils.isNull(comments) ? "" : "\n**Comments**\n" + reporter.getMentionTag() + ": `" + comments + "`\n")
                + "\n" + LoggingCategory.getTime();
        
        EmbedBuilder embed = new EmbedBuilder()
				.setTitle("Message Report")
                .setUrl(msgLink)
				.setAuthor(reporter)
				.setDescription(description)
				.setFooter("MCO Bot | Report")
				.setColor(Color.RED);

        MessageBuilder builder = new MessageBuilder()
				.setEmbed(embed)
				.addActionRow(
					Button.danger("delete", "Delete Message"),
					Button.secondary("ignore", "Ignore")
				);
        
        CompletableFuture<Message> cf_msg = builder.send(messageReports);
        setReceiptMessage(cf_msg);
        return embed;
    }

    private void setReceiptMessage(CompletableFuture<Message> cf_msg) {
        if (cf_msg == null) {
            submitReport();
            return;
        }

        cf_msg.whenComplete((msg, err) -> {
            if (msg == null) {
                submitReport();
            } else {
                receiptMessage = msg;
                reportsByReceiptId.put(receiptMessage.getIdAsString(), this);
            }
        });
    }

    public Message getReportedMessage() {
        return reportedMessage;
    }

    public Message getReceiptMessage() {
        return receiptMessage;
    }

    public String getComments() {
        return comments;
    }

    public User getReporter() {
        return reporter;
    }

    public void settle() {
        receiptMessage.delete("This report has been settled");
        reportsByReceiptId.remove(receiptMessage.getIdAsString());
        DATA_MANAGER.unregister(this);
    }

    @Override
    public HashMap<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("receiptMessageId", receiptMessage.getIdAsString());
        data.put("reportedMessageLink", reportedMessage.getLink().toExternalForm());
        data.put("comments", comments);
        data.put("reporterId", reporter.getIdAsString());
        return data;
    }
}
