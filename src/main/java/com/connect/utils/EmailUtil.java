package com.connect.utils;

import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    public String getBody() {
        return """
                <html>
                    <head>
                        <style>
                            .container {
                                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                                padding: 20px;
                                background-color: #f4f4f4;
                                border-radius: 10px;
                                color: #333;
                            }
                            .otp-box {
                                font-size: 24px;
                                font-weight: bold;
                                background-color: #e6f7ff;
                                padding: 10px 20px;
                                border: 2px dashed #00bfff;
                                display: inline-block;
                                margin-top: 10px;
                                border-radius: 5px;
                            }
                            .footer {
                                margin-top: 20px;
                                font-size: 12px;
                                color: #888;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <h1>Hey %s 👋</h1>
                            <p>Thank you for signing up! Here is your One Time Password (OTP):</p>
                            <div class="otp-box">%s</div>
                            <p class="footer">If you didn’t request this, please ignore this email.<br>Regards, Connect Team</p>
                        </div>
                    </body>
                    </html>
                """;
    }

}
