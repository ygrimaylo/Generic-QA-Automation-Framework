package com.placester.web.steps;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.Writer;
import java.io.IOException;

/**
 * @author Yevgeniy Grimaylo Copywrights 2015
 */
public class FirefoxDriverSupport {

    private static String clearCookieHTML = new StringBuilder()
        .append("<html>\n")
        .append("<script language=\"javascript\" type=\"text/javascript\">\n")
        .append("// Clears all cookies\n")
        .append("function clearCookies()\n")
        .append("{\n")
        .append("    netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');\n")
        .append("    var cookie        = null;\n")
        .append("    var cookieManager = Components.classes[\"@mozilla.org/cookiemanager;1\"].getService(Components.interfaces.nsICookieManager);\n")
        .append("    var cookies       = cookieManager.enumerator;\n")
        .append("    var cleared       = document.getElementById('clearedCookies');\n")
        .append("    var removed       = \"\";\n")
        .append("    var count         = 0;\n")
        .append("\n")
        .append("    // Loop through the cookies\n")
        .append("    while(cookies.hasMoreElements())\n")
        .append("    {\n")
        .append("        cookie = cookies.getNext();\n")
        .append("\n")
        .append("        // If this is a cookie with no expiration\n")
        .append("        if(cookie instanceof Components.interfaces.nsICookie)\n")
        .append("        {\n")
        .append("            cookieManager.remove(cookie.host, cookie.name, cookie.path, false);\n")
        .append("            removed = removed + \"removed \" + cookie.host +\",\"+ cookie.name +\",\"+ cookie.path + \"<br>\";\n")
        .append("            count = count + 1;\n")
        .append("        }\n")
        .append("        removed = removed + \"<br>Total: removed \" + count + \" cookies\";\n")
        .append("    }\n")
        .append("    cleared.innerHTML = removed;\n")
        .append("}\n")
        .append("</script>  \n")
        .append("<body onLoad=\"clearCookies();\">\n")
        .append("<div id=\"clearedCookies\">Nothing Happened.  This is Bad!!!!</div>\n")
        .append("</body>\n")
        .append("</html>\n").toString();


        public static File writeClearCookiesHTML(File profileDir) throws IOException {
            File html = new File(profileDir, "clearCookies.html");
            Writer output = null;
            try {
                output = new BufferedWriter(new FileWriter(html));
                output.write(clearCookieHTML);
            } finally {
                if (output != null) {
                    output.close();
                }
            }
            return html;
        }
}
