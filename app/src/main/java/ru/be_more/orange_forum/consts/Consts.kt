package ru.be_more.orange_forum.consts

//fragment types
const val POST_IN_BOARD_TAG = "POST_IN_BOARD_FRAGMENT"
const val POST_IN_THREAD_TAG = "POST_IN_THREAD_FRAGMENT"

//Navigation bundle
const val NAVIGATION_BOARD_ID = "NAVIGATION_ID"
const val NAVIGATION_BOARD_NAME = "NAVIGATION_NAME"
const val NAVIGATION_THREAD_NUM = "NAVIGATION_THREAD_NUM"
const val NAVIGATION_THREAD_TITLE = "NAVIGATION_THREAD_TITLE"
const val NAVIGATION_TITLE = "title"

//push notification channel
const val CHANNEL_ID = "4e8a3b3c8d5c3d6cffb841e9bf7da63"

//Base URL
const val DVACH_ROOT_URL = "https://2ch.hk/"

//temp cookie for 18+ access
const val COOKIE = "usercode_auth=54e8a3b3c8d5c3d6cffb841e9bf7da63; _ga=GA1.2.57010468.1498700728; ageallow=1; _gid=GA1.2.1910512907.1585793763; _gat=1"

//todo to prefs when settings
const val ThreadUpdateInterval = 30L

//HTML for recaptcha
const val PAGE_HTML = "<html>\n" +
        "<head>\n" +
        "    <script type=\"text/javascript\">\n" +
        "    var sendParams = function() {\n" +
        "       var responseEl = document.getElementById(\"g-recaptcha-response\");\n" +
        "\t   var response = responseEl.value;\n" +
        "\t   return JSON.stringify(response);\n" +
        "    };\n" +
        "  </script>\n" +
        "    <script type=\"text/javascript\">\n" +
        "      var onloadCallback = function() {\n" +
        "         grecaptcha.render('html_element', {\n" +
        "          'sitekey' : '6LeQYz4UAAAAAL8JCk35wHSv6cuEV5PyLhI6IxsM' \n" +
        "        });" +
        "      };\n" +
        "  </script>\n" +
        "</head>\n" +
        "<body style=\"\nbackground: #eeeeee;\">\n" +
        "<form action=\"?\" method=\"POST\">\n\n" +
        "      <div id=\"html_element\"></div>" +
        "      <br/>\n" +
        "    </form>" +
        "<script src=\"//www.google.com/recaptcha/api.js?onload=onloadCallback\"\n" + "&render=explicit" +
        "        async defer>\n" +
        "</script>\n" +
        "</body>\n" +
        "</html>\n" +
        "\n"