package TowDef.Units.Towers;

/**
 * Created by Faria on 7/10/2016.
 */

import TowDef.Active;
import TowDef.GUI.GUI;
import TowDef.Game.TimeLine;
import TowDef.Units.Intruders.Intruder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;



import TowDef.Active;
import TowDef.GUI.GUI;
import TowDef.Game.TimeLine;
import TowDef.Units.Intruders.Intruder;
import javafx.scene.image.ImageView;

/**
 * Created by Faria on 7/10/2016.
 */
public class Bullet implements Active {
    private ImageView imageView;
    private Intruder choice;
    private int damage;
    public final int speed;
    public Bullet(ImageView imageView, Intruder choice,int damage, int speed){
        this.imageView = imageView;
        this.choice = choice;
        this.damage = damage;
        TimeLine.getInstance().addOtherActives(this);
        this.speed = speed;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void action(){
        GUI.getInstance().getGameGraphics().shooting(choice,damage, this);
    }

    public void delete(){
        TimeLine.getInstance().removeActive(this);
        imageView.setImage(new Image(getClass().getResource("/files/media/6.gif").toString()));

    }
}
