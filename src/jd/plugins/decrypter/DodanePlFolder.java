//jDownloader - Downloadmanager
//Copyright (C) 2013  JD-Team support@jdownloader.org
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jd.plugins.decrypter;

import java.util.ArrayList;

import jd.PluginWrapper;
import jd.controlling.ProgressController;
import jd.parser.Regex;
import jd.plugins.CryptedLink;
import jd.plugins.DecrypterPlugin;
import jd.plugins.DownloadLink;
import jd.plugins.FilePackage;
import jd.plugins.PluginForDecrypt;

@DecrypterPlugin(revision = "$Revision$", interfaceVersion = 2, names = { "dodane.pl" }, urls = { "http://dodane\\.pl/folder/([0-9]+)/(.*)" }, flags = { 0 })
public class DodanePlFolder extends PluginForDecrypt {

    private String MAIN_PAGE = "http://dodane.pl/";

    public DodanePlFolder(PluginWrapper wrapper) {
        super(wrapper);
    }

    public ArrayList<DownloadLink> decryptIt(CryptedLink param, ProgressController progress) throws Exception {
        ArrayList<DownloadLink> decryptedLinks = new ArrayList<DownloadLink>();
        String parameter = param.toString();
        int numberOfFolders = 0;
        int numberOfFiles = 0;
        br.setFollowRedirects(true);
        br.getPage(parameter);
        final String folderId = new Regex(parameter, "http://dodane\\.pl/folder/([0-9]+)/.*").getMatch(0);
        final String folderName = new Regex(parameter, "http://dodane\\.pl/folder/[0-9]+/(.*)").getMatch(0);

        String folderClass = br.getRegex("<div class=\"folder-name\"[ \n\t\r]*folderid=\"" + folderId + "\">[ \n\t\r]*<a[ \n\t\r]*href=\"+" + parameter.replace(MAIN_PAGE, "") + "\"[ \n\t\r]*class=\"active\">(.*?)</a>").getMatch(0);
        String folderInfo = new Regex(br, "<div class=\"files\\-container\">(.*?)<div style=\"width:90%;float:right;margin:20px 15px 0 0;\">").getMatch(0);

        String[][] folders = new Regex(folderInfo, "class=\"folder-pos \"[ \n\t\r]*folderid=\"[0-9]+\">[ \n\t\r]*<a href=\"(.*?)\">").getMatches();
        if (folders != null && folders.length != 0) {

            for (String[] folder : folders) {
                DownloadLink dl = createDownloadlink(MAIN_PAGE + folder[0]);
                decryptedLinks.add(dl);
                numberOfFolders++;
            }
        }
        final String[] files = new Regex(folderInfo, "<div class=\"file\\-pos (.*?)class=\"separator\"").getColumn(0);
        if (files != null && files.length != 0) {
            FilePackage fp = FilePackage.getInstance();
            final String currentFolderId = br.getRegex("var currentFolderID = \"(\\d+)\";").getMatch(0);
            String packageName = br.getRegex("var currentFolderName = \"([^<>\"]*?)\"").getMatch(0);
            if (packageName == null) {
                packageName = currentFolderId;
            }

            fp.setName(packageName);
            fp.setProperty("ALLOW_MERGE", true);

            for (String file : files) {

                String link = new Regex(file, "<a href=\"(/file/[^<>\"]*?)\"").getMatch(0);
                link = MAIN_PAGE + link.replace("/file", "file");
                DownloadLink dl = createDownloadlink(link);
                try {
                    distribute(dl);
                } catch (final Throwable e) {
                    // Not available in old Stable
                }
                decryptedLinks.add(dl);
                fp.add(dl);
                numberOfFiles++;
            }
        }
        return decryptedLinks;

    }

    String findFoldersNames(String currentFolderId, String currentName) {
        String newName = currentName;
        String crFolderId = currentFolderId;
        while (true) {
            String parentFolderId = new Regex(br, "<tr myid=\"" + crFolderId + "\"[ \n\t\r]+parentid=\"(\\d+)\">").getMatch(0);
            String parentFolderName = new Regex(br, "<div class=\"folder-name\"[ \n\t\r]+folderid=\"" + parentFolderId + "\">[ \n\t\r]+<a[ \n\t\r]+href=\"folder/" + parentFolderId + "/(.*?)\"[ \n\t\r]+>(.*?)</a>").getMatch(1);

            if (parentFolderId == null) {
                break;
            }
            newName = parentFolderName + "+" + newName;

            crFolderId = parentFolderId;
        }
        return newName;
    }

    /*
     * NO OVERRIDE!!
     */
    public boolean hasCaptcha(CryptedLink link, jd.plugins.Account acc) {
        return false;
    }

}