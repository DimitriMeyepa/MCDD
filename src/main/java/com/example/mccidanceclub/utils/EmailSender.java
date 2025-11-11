package com.example.mccidanceclub.utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class EmailSender {

    // Méthode pour envoyer l'email AVEC PDF
    public static void sendRegistrationEmail(String recipientEmail, String firstName, File pdfFile) {

        String host = "smtp.gmail.com";
        String from = "mccidanceclub.noreply@gmail.com";
        String password = "lomhqrxbzimrixuo"; // mot de passe d'application Gmail

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", host);
        props.put("mail.debug", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "MCCI Dance Club"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Bienvenue au MCCI Dance Club !");

            // Partie texte
            MimeBodyPart textPart = new MimeBodyPart();
            String content = "Bonjour " + firstName + ",\n\n" +
                    "Félicitations et bienvenue au MCCI Dance Club !\n\n" +
                    "Vous êtes désormais officiellement inscrit au club et nous sommes ravis de vous compter parmi nos membres.\n\n" +
                    "Nous vous tiendrons informé de nos prochains événements et activités.\n\n" +
                    "Si vous avez des questions, n’hésitez pas à nous contacter à l’adresse : mccidanceclub@outlook.com\n\n" +
                    "À très bientôt sur la piste de danse !";
            textPart.setText(content);

            // Partie pièce jointe (PDF)
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(pdfFile);

            // Assemblage du message
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            // Envoi
            Transport.send(message);

            System.out.println("✅ Email avec PDF envoyé avec succès à " + recipientEmail);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email :");
            e.printStackTrace();
        }
    }

}
