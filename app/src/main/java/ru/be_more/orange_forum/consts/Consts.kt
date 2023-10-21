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
const val DVACH_ROOT_URL = "https://2ch.hk"

    //FIXME
//temp cookie for 18+ access
//const val COOKIE = "usercode_auth=54e8a3b3c8d5c3d6cffb841e9bf7da63; _ga=GA1.2.57010468.1498700728; ageallow=1; _gid=GA1.2.1910512907.1585793763; _gat=1"

const val COOKIE = "_ga=GA1.2.191888855.1677251490; _gid=GA1.2.1275785988.1677251490; usercode_auth=e29b3bb66af1f3d861f4f65d40f235c1; ageallow=1; _csrf=s%3AyLbpAWDgsiF0DkWetCuIdIiR.YOIJ94PeD7Vl%2F76QFvoFh9PEnmrtsBcPIRuoF73eIC4; cf_clearance=t.jhYOj5uTNL4bz8aln1kTy3XymGdT1H1BO9cXkDnPk-1697612559-0-1-d90339e1.ccda1edc.6e3502af-0.2.1697612559; _ga_7NPYTX0FY3=GS1.2.1697835378.1144.1.1697835386.0.0.0"


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