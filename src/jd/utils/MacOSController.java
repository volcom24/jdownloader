//    jDownloader - Downloadmanager
//    Copyright (C) 2008  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jd.utils;

import jd.gui.skins.simple.AboutDialog;
import jd.gui.skins.simple.GuiRunnable;
import jd.gui.skins.simple.SimpleGUI;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

public class MacOSController extends Application {

    public MacOSController() {
        // setEnabledPreferencesMenu(true);
        setEnabledAboutMenu(true);
        addApplicationListener(new Handler());
    }

    class Handler extends ApplicationAdapter {

        public void handleQuit(ApplicationEvent e) {
            JDUtilities.getController().exit();
        }

        public void handleAbout(ApplicationEvent e) {
            e.setHandled(true);
            new GuiRunnable<Object>() {

                @Override
                public Object runSave() {
                    new AboutDialog();
                    return null;
                }

            }.start();
        }

        // public void handlePreferences(ApplicationEvent e) {
        // SimpleGUI.CURRENTGUI.actionPerformed(new ActionEvent(this,
        // JDAction.APP_CONFIGURATION, null));
        // }

        public void handleReOpenApplication(ApplicationEvent e) {
            if (SimpleGUI.CURRENTGUI.isVisible() == false) {
                SimpleGUI.CURRENTGUI.setVisible(true);
            }
        }

    }

}
