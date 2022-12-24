package com.lemonico.core.utils;



import com.lemonico.common.bean.MailBean;
import org.springframework.stereotype.Component;

/**
 * メールテンプレート
 *
 * @since 1.0.0
 */
@Component
public class MailTools
{

    /**
     * ユーザー新規登録テンプレート
     *
     * @param email 送信先
     * @param domain 認証ドメイン
     * @return 送信メール情報
     * @since 1.0.0
     */
    public MailBean registerTemplate(String email, String domain) {
        // 邮箱加密
        String aesEncodedEmail = PasswordHelper.AESEncode(email);
        MailBean mailBean = new MailBean();
        mailBean.setRecipient(new String[] {
            email
        });
        mailBean.setSubject("ユーザー登録を完了願います");
        String sb = "<h1>■新規登録</h1>" +
            "<h4 style='font-weight:normal'>この度はサンロジにご登録頂きありがとうございました。<h4/>" +
            "<h4 style='font-weight:normal'>以下のURLを8時間以内にクリックして登録手続きを完了してください。<h4/>" +
            "<h4 style='font-weight:normal'>8時間以内にご登録手続きが完了されない場合、自動的に無効となります。<h4/>" +
            " " +
            "<h3><a href='" + domain + "/register_info/?email=" + aesEncodedEmail + "'" + ">" + domain
            + "/register_info/?email=" + aesEncodedEmail + "</a></h3>" +
            " " +
            "<h4 style='font-weight:normal'>ご登録が完了しますと、サンロジのサービスを利用することができます。<h4/>" +
            "<h4 style='font-weight:normal'>ご利用に際してご不明な点やご質問がありましたら<h4/>" +
            "<h4 style='font-weight:normal'>お気軽にお問い合わせください。<h4/>" +
            " " +
            "<h4 style='font-weight:normal'>引き続き、サンロジをよろしく御願い致します。<h4/>" +
            " " +
            "<h4 style='font-weight:normal'>サンロジ カスタマーサポート\n" +
            "お問い合わせ<h4/>" +
            "<h3><a href='" + domain + "/support'>" + domain + "/support<a/><h3/>" +
            " " +
            "<h4 style='font-weight:normal'>サポート対応時間：平日10:00～18:00<h4/>" +
            "<h4 style='font-weight:normal'>※こちらのメールはシステムから自動で送信されております<h4>";
        mailBean.setContent(sb);
        return mailBean;
    }

    /**
     * パスワード変更テンプレート
     *
     * @param email 送信先
     * @param domain 認証ドメイン
     * @return 送信メール情報
     * @since 1.0.0
     */
    public MailBean changePwdTemplate(String email, String domain) {
        // 邮箱加密
        String aesEncode = PasswordHelper.AESEncode(email);
        MailBean mailBean = new MailBean();
        mailBean.setRecipient(new String[] {
            email
        });
        mailBean.setSubject("【SunLogi】パスワードの再発行");
        String sb = "<h1>【SunLogi】パスワードの再発行</h1>" +
            "<h4 style='font-weight:normal'>サンロジをご利用頂きありがとうございます。<h4/>" +
            "<h4 style='font-weight:normal'>以下のURLを1時間以内にクリックして新しいパスワードを設定してください。<h4/>" +
            "<h4 style='font-weight:normal'>1時間以内に変更手続きが完了されない場合、自動的に無効となります。<h4/>" +
            " " +
            "<h6><a href='" + domain + "/change_password/" + "?email=" + aesEncode + "'" + ">" + domain
            + "/change_password/" + "?email=" + aesEncode + "</a></h6>" +
            " " +
            "<h4 style='font-weight:normal'>ご利用に際してご不明な点やご質問がありましたら、お気軽にお問い合わせください。<h4/>" +
            "<h4 style='font-weight:normal'>引き続き、サンロジをよろしく御願い致します。<h4/>" +
            " " +
            "<h4 style='font-weight:normal'>=================================<h4/>" +
            " " +
            "<h4 style='font-weight:normal'>サンロジ カスタマーサポート<h4/>" +
            "<h4 style='font-weight:normal'>お問い合わせ<h4/>" +
            "<h3><a href='" + domain + "/support'>" + domain + "/support<a/><h3/>" +
            " " +
            "<h4 style='font-weight:normal'>サポート対応時間：平日10:00～18:00<h4/>" +
            "<h4 style='font-weight:normal'>※こちらのメールはシステムから自動で送信されております。<h4/>";
        mailBean.setContent(sb);
        return mailBean;
    }

    /**
     * ユーザー有効認証処理
     *
     * @param email 送信先
     * @param pwd パスワード
     * @param domain サーバードメイン
     * @param oldPwdFlg 旧パスワードフラグ
     * @return 送信メール情報
     * @since 1.0.0
     */
    public MailBean remindUserTemplate(String email, String pwd, String domain, Boolean oldPwdFlg) {
        MailBean mailBean = new MailBean();
        mailBean.setRecipient(new String[] {
            email
        });
        mailBean.setSubject("ご利用案内");
        StringBuilder sb = new StringBuilder();
        if (oldPwdFlg) {
            sb.append("<h1>■登録完了</h1>")
                .append(" ")
                .append("<h4 style='font-weight:normal'>サンロジカスタマーサポートです。<h4/>")
                .append("<h4 style='font-weight:normal'>この度はLemonicoにご登録いただき、誠にありがとうございます。<h4/>")
                .append("<h3 style='font-weight:normal'>アカウントに登録する：").append(email).append("<h3/>")
                .append(" ")
                .append("<h4 style='font-weight:normal'>お客さまを適切な倉庫にご案内するために、ご利用開始まで次のような手順を取らせて頂いています。<h4/>")
                .append(" ")
                .append("<h4 style='font-weight:normal'>1. ご登録いただいた情報を営業担当が確認します。<h4/>")
                .append("<h4 style='font-weight:normal'>2. 電話にて、お客さまの取り扱い商材、規模、特別な運用の有無など、業務詳細についてヒアリングさせて頂きます。<h4/>")
                .append("<h4 style='font-weight:normal'>3. サンロジより提携先倉庫に連絡し、お客さまから頂いた情報を元に担当する倉庫を決定します。<h4/>")
                .append("<h4 style='font-weight:normal'>4. お客さまにて管理画面よりアカウント情報をご登録いただきます。<h4/>")
                .append("<h4 style='font-weight:normal'>5. ご利用開始いただけます。<h4/>")
                .append(" ")
                .append("<h4 style='font-weight:normal'>新規アカウントの登録を頂いた順番に従い、弊社営業担当者からお電話いたします。<h4/>")
                .append("<h4 style='font-weight:normal'>電話時間は、平日10時〜18時を予定しています。<h4/>")
                .append("<h4 style='font-weight:normal'>お客様の業務内容や荷物量により、順番が前後することがございます。ご了承ください。<h4/>")
                .append(" ")
                .append("<h4 style='font-weight:normal'>お急ぎのところ大変申し訳ありませんが、お電話をお待ち頂ますよう何卒よろしくお願い申し上げます。<h4/>")
                .append(" ")
                .append("<h4 style='font-weight:normal'>サンロジ カスタマーサポート<h4/>")
                .append("<h4 style='font-weight:normal'>お問い合わせ<h4/>").append("<h3><a href='").append(domain)
                .append("/support'>").append(domain).append("/support<a/><h3/>")
                .append(" ")
                .append("<h4 style='font-weight:normal'>サポート対応時間：平日10:00～18:00<h4/>")
                .append("<h4 style='font-weight:normal'>※こちらのメールはシステムから自動で送信されております。<h4/>");

        } else {
            sb.append("<h1>■登録完了</h1>")
                .append(" ")
                .append("<h4 style='font-weight:normal'>サンロジカスタマーサポートです。<h4/>")
                .append("<h4 style='font-weight:normal'>この度はSUNLOGIにご登録いただき、誠にありがとうございます。<h4/>")
                .append("<h3 style='font-weight:normal'>アカウントに登録する：").append(email).append("  パスワード：").append(pwd)
                .append("<h3/>")
                .append(" ")
                .append("<h4 style='font-weight:normal'>お客さまを適切な倉庫にご案内するために、ご利用開始まで次のような手順を取らせて頂いています。<h4/>")
                .append(" ")
                .append("<h4 style='font-weight:normal'>1. ご登録いただいた情報を営業担当が確認します。<h4/>")
                .append("<h4 style='font-weight:normal'>2. 電話にて、お客さまの取り扱い商材、規模、特別な運用の有無など、業務詳細についてヒアリングさせて頂きます。<h4/>")
                .append("<h4 style='font-weight:normal'>3. サンロジより提携先倉庫に連絡し、お客さまから頂いた情報を元に担当する倉庫を決定します。<h4/>")
                .append("<h4 style='font-weight:normal'>4. お客さまにて管理画面よりアカウント情報をご登録いただきます。<h4/>")
                .append("<h4 style='font-weight:normal'>5. ご利用開始いただけます。<h4/>")
                .append(" ")
                .append("<h4 style='font-weight:normal'>新規アカウントの登録を頂いた順番に従い、弊社営業担当者からお電話いたします。<h4/>")
                .append("<h4 style='font-weight:normal'>電話時間は、平日10時〜18時を予定しています。<h4/>")
                .append("<h4 style='font-weight:normal'>お客様の業務内容や荷物量により、順番が前後することがございます。ご了承ください。<h4/>")
                .append(" ")
                .append("<h4 style='font-weight:normal'>お急ぎのところ大変申し訳ありませんが、お電話をお待ち頂ますよう何卒よろしくお願い申し上げます。<h4/>")
                .append(" ")
                .append("<h4 style='font-weight:normal'>サンロジ カスタマーサポート<h4/>")
                .append("<h4 style='font-weight:normal'>お問い合わせ<h4/>").append("<h3><a href='").append(domain)
                .append("/support'>").append(domain).append("/support<a/><h3/>")
                .append(" ")
                .append("<h4 style='font-weight:normal'>サポート対応時間：平日10:00～18:00<h4/>")
                .append("<h4 style='font-weight:normal'>※こちらのメールはシステムから自動で送信されております。<h4/>");
        }
        mailBean.setContent(String.valueOf(sb));
        return mailBean;
    }

    /**
     * アカウント情報発行のお知らせ
     *
     * @param email 送信先
     * @param pwd パスワード
     * @return 送信メール情報
     * @since 1.0.0
     */
    public MailBean sendUserEmail(String email, String pwd) {
        MailBean mailBean = new MailBean();
        mailBean.setRecipient(new String[] {
            email
        });
        mailBean.setSubject("【SunLogi】アカウント情報発行のお知らせ");
        String sb = "<h4 style='font-weight:normal'>いつもSunLogiをご利用いただき、ありがとうございます。<h4/>" +
            " " +
            "<h4 style='font-weight:normal'>SunLogiのアカウント情報の発行が完了いたしました。<h4/>" +
            "<h4 style='font-weight:normal'>https://sunlogi.com<h4/>" +
            " " +
            "<h4 style='font-weight:normal'>SunLogiにログインされる際は、下記の登録メールアドレスをご利用ください。<h4/>" +
            "<h4 style='font-weight:normal'>" + email + "<h4/>" +
            "<h4 style='font-weight:normal'>パスワードが以下となりますので、合わせてご利用ください。<h4/>" +
            "<h4 style='font-weight:normal'>" + pwd + "<h4/>" +
            "<h4 style='font-weight:normal'>※メールアドレス認証後に、アカウントの安全のためパスワードを変更してください。<h4/>" +
            " " +
            "<h4 style='font-weight:normal'>===================================<h4/>" +
            "<h4 style='font-weight:normal'>サンロジ カスタマーサポート<h4/>" +
            "<h4 style='font-weight:normal'>お問い合わせ<h4/>" +
            "<h4 style='font-weight:normal'>https://www.sunlogi.com/support<h4/>" +
            " " +
            "<h4 style='font-weight:normal'>サポート対応時間：平日10:00～18:00<h4/>" +
            "<h4 style='font-weight:normal'>※こちらのメールはシステムから自動で送信されております。<h4/>" +
            "<h4 style='font-weight:normal'>※このメールに心あたりがない場合は、このメールは破棄してください。<h4/>";
        mailBean.setContent(sb);
        return mailBean;
    }
}
