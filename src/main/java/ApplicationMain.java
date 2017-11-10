/**
 * Created by Thales on 12/09/2017.
 */
import controllers.AppUserController;
import controllers.CryptoController;
import controllers.KeyController;
import controllers.UserController;
import services.AppUserService;
import services.CryptoService;
import services.KeyService;
import services.UserService;
import utils.Utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class ApplicationMain extends JDialog
{
    private static final long serialVersionUID = 1L;

    public ApplicationMain()
    {
        //Create a frame
        Frame f = new Frame();
        f.setSize(500, 300);
        f.setTitle("VAE Rest API");

        //Write something
        f.add(Utils.logger.getSp());

        //Make visible
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

    }
    public static void main(final String[] args)
    {
        Thread ui = new Thread() {
            public void run() {
                new ApplicationMain();
            }
        };
        Thread keyManager = new Thread() {
            public void run() {
                new CryptoController(new CryptoService());
            }
        };
        Thread cryptoManager = new Thread() {
            public void run() {
                new KeyController(new KeyService());
            }
        };
        Thread appUserManager = new Thread() {
            public void run() {
                new AppUserController(new AppUserService());
            }
        };
        ui.start();
        keyManager.start();
        cryptoManager.start();
        appUserManager.start();
        System.out.println("END MAIN");
    }
}
