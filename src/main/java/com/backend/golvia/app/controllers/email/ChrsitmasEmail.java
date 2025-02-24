package com.backend.golvia.app.controllers.email;

public class ChrsitmasEmail {
    public static String build(){
        return """
                <!DOCTYPE html>
                <html dir="ltr" xmlns="http://www.w3.org/1999/xhtml">
                
                <head>
                  <meta charset="UTF-8">
                  <meta content="width=device-width, initial-scale=1" name="viewport">
                  <meta name="x-apple-disable-message-reformatting">
                  <meta http-equiv="X-UA-Compatible" content="IE=edge">
                  <link rel="preconnect" href="https://fonts.googleapis.com">
                  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                  <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@100..900&display=swap" rel="stylesheet">
                </head>
                
                <body data-id="__react-email-body" style="margin: 0; font-family: outfit, sans-serif; font-weight: 300; -webkit-font-smoothing: antialiased; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; background-color: #FFF8E6"">
                  <center style="width: 100%; height: 100%; table-layout: fixed; background-color: #FFF8E6">
                    <table role="presentation" width="100%" cellspacing="0" cellpadding="0"
                      style="mso-table-lspace: 0pt; mso-table-rspace: 0pt; margin: 0 auto; width: 100%; border-spacing: 0; padding: 0%;"
                      class="main" data-id="__react-email-container">
                      <tbody>
                        <tr>
                          <td>
                            <img data-id="react-email-img" src="https://res.cloudinary.com/dvu4qhyqq/image/upload/holiday_y9zzbj.jpg"
                              style="width:100%" />
                          </td>
                        </tr>
                      </tbody>
                    </table>
                    <table role="presentation" cellspacing="0" cellpadding="0" align="center"
                      style="border-spacing: 0; mso-table-lspace: 0pt; mso-table-rspace: 0pt; padding-top: 32px; padding-bottom: 50px;">
                      <tbody>
                        <tr>
                          <td align="center" style="padding: 0;">
                            <table role="presentation" style="border-spacing: 0; mso-table-lspace: 0pt; mso-table-rspace: 0pt;">
                              <tr>
                                <td style="padding: 0;">
                                  <p data-id="react-email-text"
                                    style="margin: 0; font-size: 14px; font-weight: 300; color: #111111EB; margin-bottom: 10px; font-family: outfit;">
                                    Find us
                                    online</p>
                                </td>
                              </tr>
                            </table>
                
                            <table role="presentation" cellspacing="16"
                              style="border-spacing: 0; mso-table-lspace: 0pt; mso-table-rspace: 0pt;">
                              <tr>
                                <div style="display:flex; column-gap:10px; align-items: center; ">
                                  <div width="35">
                                    <a href="https://www.instagram.com/golvia.in">
                                      <img src="https://res.cloudinary.com/dvu4qhyqq/image/upload/instagram_bonioq.png"
                                        srcset="https://res.cloudinary.com/dvu4qhyqq/image/upload/instagram_kvvlms.svg" alt="instagram"
                                        width="35" height="35">
                                    </a>
                                  </div>
                                  <div>
                                    <a href="https://x.com/golviainc">
                                      <img src="https://res.cloudinary.com/dvu4qhyqq/image/upload/x_cgzy2b.png"
                                        srcset="https://res.cloudinary.com/dvu4qhyqq/image/upload/x_it78wz.svg" alt="x" width="35"
                                        height="35">
                                    </a>
                                  </div>
                                  <div>
                                    <a href="https://www.linkedin.com/company/golvia-inc">
                                      <img src="https://res.cloudinary.com/dvu4qhyqq/image/upload/linkedin_ton6zc.png"
                                        srcset="https://res.cloudinary.com/dvu4qhyqq/image/upload/linkedin_bx7bqh.svg" alt="x"
                                        width="35" height="35">
                                    </a>
                                  </div>
                                  <div>
                                    <a href="https://www.tiktok.com/@golvia.inc">
                                      <img src="https://res.cloudinary.com/dvu4qhyqq/image/upload/tiktok_jqkktx.png"
                                        srcset="https://res.cloudinary.com/dvu4qhyqq/image/upload/tittok_cjxeco.svg" alt="x" width="35"
                                        height="35">
                                    </a>
                                  </div>
                                </div>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </center>
                </body>
                
                </html>""";
    }
}
