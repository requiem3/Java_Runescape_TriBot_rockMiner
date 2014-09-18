package scripts;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Screen;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;

import org.tribot.api.rs3.Camera;
import org.tribot.api.rs3.Player;
import org.tribot.api.rs3.ScreenModels;
import org.tribot.api.rs3.types.ScreenModel;
import org.tribot.api.types.generic.CustomRet_0P;
import org.tribot.script.Script;
import org.tribot.api.rs3.Backpack;

import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

import java.awt.*;

@ScriptManifest(authors = "Requiem", category = "Test", name = "RqMiner")
public class powerMiner extends Script implements Painting {
    int rocksMined = 0;

    /*
    final double[][] rocks = { //Object id's, exp per
            {1827131691L, 366615370L, 2718818122L, 17.5} //Copper rocks,
    };
    */
    public void onPaint (Graphics g)
    {
        long runTime = getRunningTime();

        g.setColor(Color.white);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 25));

        Dimension d = Screen.getDimension();
        g.drawString("Rocks Mined: " + rocksMined, d.width - 600, d.height - 155);
        g.drawString("Run Time: " + Timing.msToString(runTime), d.width - 600, d.height - 135);
    }

    @Override
    public void run() {
        Mouse.setSpeed(150);
        Camera.setPitch(true);

        while (true) {
            int c = getState();
            sleep(50);

            switch(c)
            {
                case 1: drop();
                        break;
                case 2: mine();
                        break;
                case 3: sleep(1200, 1500); //Player is in mining animation
                        break;
            }
        }
    }

    public void drop()
    {
        General.println(Backpack.find(RqVariables.copperItem).length);
        rocksMined += Backpack.find(RqVariables.copperItem).length; //add how many rocks are in bag to amount mined
        Backpack.drop(RqVariables.copperItem);                      //drop rocks in bag
        rocksMined -= Backpack.find(RqVariables.copperItem).length + 1; //subtract how many are left from misclicks

        return;
    }

    public void mine()
    {
        General.println("in Mine() function");

        if(DynamicClicking.clickScreenModel(

                new CustomRet_0P<ScreenModel>() {

                    @Override
                    public ScreenModel ret() {
                        ScreenModel[] m = ScreenModels.findNearest(RqVariables.Copper[0],
                                RqVariables.Copper[1], RqVariables.Copper[2]);
                        if (m.length < 1) {
                            return null; //no rocks found return null
                        }
                        return m[0]; //return the closest rock found
                    }
                }, 3))
        {
            if (Timing.waitChooseOption("Mine", 1000)) //rocks found mine and wait
            {
                sleep(1800, 2100);
                return;
            }
            else if (Timing.waitChooseOption("cancel", 1000)); //clicked next to rocks close window
            else return;
        }
        else //Rocks are still respawning return back to loop
        {
            sleep(900, 1100);
            return;
        }
        return;
    }

    //Get current state of the player, Standing still, Mining
    public int getState()
    {
        General.println("In getState() function");
        if(Backpack.getAll().length > 11)
        {
            return 1;
        }
        else if(Player.getAnimation() == -1) //standing still
        {
            return 2;
        }
        else if(Player.getAnimation() == 626 || Player.getAnimation() == 624) //mining
        {
            sleep(850, 1150); //currently mining so waiting a little doesnt hurt the cpu lol
            return 3;
        }
        else
        {
            sleep(1500, 1700);
            return 0;
        }
    }
}