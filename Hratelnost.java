import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Graphics2D;

public class Hratelnost extends JPanel implements KeyListener, ActionListener {

    private boolean hrát = false; //aby, když spustíme hru, tak nehrála sama//

    private int skóre = 0; //skóre na začátku hry//

    private int všechnyCihly = 21; //všechny cihly ve hře//

    private Timer stopky; //tvorba stopek//
    private GenerátorSítě síť; //tvorba sítě//

    private int delay = 3; //určuje nám rychlost koule//

    private int hráčX = 310; //začínající pozice naší plošiny//

    private int pozicekouleX = 180; //začínající pozice naší koule v X//
    private int pozicekouleY = 380; //začínající pozice naší koule v Y//

    private int směrkouleX = -1; //kam bude koule směřovat v X//
    private int směrkouleY = -2; //kam bude koule směřovat v Y//

    public Hratelnost () { //přidělujeme konstruktor//

        addKeyListener (this);
        setFocusable (true);
        setFocusTraversalKeysEnabled (false);

        síť = new GenerátorSítě (3, 7);

        stopky = new Timer (delay, this);
        stopky.start ();
    }

    public void paint (Graphics grafika) {

        //tvorba ohraničení//
        grafika.setColor (Color.red);
        grafika.fillRect (0, 0, 692, 3);
        grafika.fillRect (0, 0, 3, 592);
        grafika.fillRect (691, 0, 3, 592);

        //tvorba pozadí//
        grafika.setColor (Color.black);
        grafika.fillRect (1, 1, 692, 592);

        //tvorba cihly//
        síť.draw ( (Graphics2D) grafika);

        //tvorba koule//
        grafika.setColor (Color.blue);
        grafika.fillOval (pozicekouleX, pozicekouleY, 22, 22);

        if (všechnyCihly <= 0) {
            hrát = false;
            směrkouleX = 0;
            směrkouleY = 0;

            grafika.setColor(Color.red);
            grafika.setFont (new Font ("dialoginput", Font.BOLD, 29) );
            grafika.drawString ("Vyhráli jste ! Dosáhli jste skóre:" + skóre, 65, 300);

            grafika.setFont (new Font ("dialoginput", Font.BOLD, 24) );
            grafika.drawString ("Stiskněte MEZERNÍK pro novou hru", 118, 360);
        }

        if (pozicekouleY > 570) {
            hrát = false;
            směrkouleX = 0;
            směrkouleY = 0;

            grafika.setColor(Color.red);
            grafika.setFont (new Font ("dialoginput", Font.BOLD, 29) );
            grafika.drawString ("Konec Hry :-( Dosáhli jste skóre:" + skóre, 65, 300);

            grafika.setFont (new Font ("dialoginput", Font.BOLD, 24) );
            grafika.drawString ("Stiskněte MEZERNÍK pro novou hru", 118, 360);
        }

        //tvorba plošiny//
        grafika.setColor (Color.red);
        grafika.fillRect (hráčX, 550, 90, 9);

        //tvroba skóre//
        grafika.setColor (Color.red);
        grafika.setFont ( new Font ("dialoginput", Font.BOLD, 27) );
        grafika.drawString ("" + skóre, 635, 35);
    }

    @Override
    public void keyPressed (KeyEvent e) {

        if (e.getKeyCode () == KeyEvent.VK_RIGHT) { //k rozpoznání, jestli byla zmáčknuta pravá klávesa šipky//

            if (hráčX >= 600) {
                hráčX = 600;
            }

            else {
                jdiDoprava ();
            }
        }

        if (e.getKeyCode () == KeyEvent.VK_LEFT) { //k rozpoznání, jestli byla zmáčknuta levá klávesa šipky//

            if (hráčX <= 10) {
                hráčX = 10 ;
            }

            else {
                jdiDoleva ();
            }
        }

        if (e.getKeyCode () == KeyEvent.VK_SPACE) { //tvorba restartu hry zapomocí mezerníku//

            if (!hrát) {
                hrát = true;
                hráčX = 310;
                skóre = 0;
                všechnyCihly = 21;
                síť = new GenerátorSítě (3, 7);
                směrkouleX = -1;
                směrkouleY = -2;
                pozicekouleX = 120;
                pozicekouleY = 350;

                repaint ();
            }
        }
    }

    public void jdiDoprava () {
        hrát = true;
        hráčX += 20;
    }

    public void jdiDoleva () {
        hrát = true;
        hráčX -= 20;
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        stopky.start (); //spuštení stopek//

        if (hrát) {

            if (new Rectangle (pozicekouleX, pozicekouleY, 20, 20). intersects (new Rectangle (hráčX, 550, 100, 8) ) ) {
                směrkouleY = -směrkouleY;
            }

            int f;
            for (f = 0; f < síť.síť.length; f++) {

                int g;
                for (g = 0; g < síť.síť [0].length; g++) {

                    if (síť.síť [f][g] > 0) {
                        int šířkaCihly = síť.šířkaCihly;
                        int výškaCihly = síť.výškaCihly;
                        int cihlaX = g * síť.šířkaCihly + 80;
                        int cihlaY = f * síť.výškaCihly + 50;

                        Rectangle obdelnik = new Rectangle (cihlaX, cihlaY, šířkaCihly, výškaCihly);
                        Rectangle cihlaObdelnik = obdelnik;
                        Rectangle kouleObdelnik = new Rectangle (pozicekouleX, pozicekouleY, 20, 20);

                        if (kouleObdelnik.intersects (cihlaObdelnik) ) {
                            síť.udejHodnotuCihly(0, f, g);
                            všechnyCihly --;
                            skóre += 10;

                            if (pozicekouleX + 19 <= cihlaObdelnik.x || pozicekouleX + 1 >= cihlaObdelnik.x + cihlaObdelnik.width) {
                                směrkouleX = -směrkouleX;
                            }
                            else {
                                směrkouleY = -směrkouleY;
                            }
                        }
                    }
                }
            }

            pozicekouleX += směrkouleX;
            pozicekouleY += směrkouleY;

            if (pozicekouleX < 0) {
                směrkouleX = -směrkouleX;
            }

            if (pozicekouleY < 0) {
                směrkouleY = -směrkouleY;
            }

            if (pozicekouleX > 670) {
                směrkouleX = -směrkouleX;
            }
        }

        repaint (); //opětovné zavolání metody paint//
    }

    @Override
    public void keyTyped (KeyEvent e) {
    }

    @Override
    public void keyReleased (KeyEvent e) {
    }
}
