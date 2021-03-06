package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gameClient.util.Range;
import gameClient.util.Range2D;
import api.game_service;
import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a very simple GUI class to present a
 * game on a graph.
 */
public class MyFrame extends JFrame {
    private int _ind;
    private Arena _ar;
    private game_service _game;
    private gameClient.util.Range2Range _w2f;
    private Image dbImage;
    private Graphics dbGraphic;

    MyFrame(String a) {
        super(a);
        int _ind = 0;
    }

    public void update(Arena ar,game_service game) {
        this._game=game;
        this._ar = ar;
        updateFrame();
    }

    /**
     * this method was added by us to customize the panel.
     * @param g -graphic.
     */
    private void drawPanel(Graphics g) {
        int x = 800, y = 60;
        Graphics2D g2 = (Graphics2D) g;
        g2.drawRect(x, y,150,90);
        g.setFont(new Font("",Font.BOLD,15));
        g2.drawString("Level:\n" + getLevel(), x+5, y+=20);
        g2.drawString("Time to end:\n"+_game.timeToEnd()/1000,x+5, y+=20);
        g2.drawString("Moves:\n" + getMoves(), x+5, y+=20);
        g2.drawString("Grade:\n" + getGrade(), x+5, y+=20);
    }

    private int getLevel() {
        JsonObject json = new JsonParser().parse(_game.toString()).getAsJsonObject();
        JsonObject gameServer = json.getAsJsonObject("GameServer");
        int level = gameServer.get("game_level").getAsInt();
        return level;
    }


    private int getMoves() {
        JsonObject json = new JsonParser().parse(_game.toString()).getAsJsonObject();
        JsonObject gameServer = json.getAsJsonObject("GameServer");
        int moves = gameServer.get("moves").getAsInt();
        return moves;
    }

    private int getGrade() {
        JsonObject json = new JsonParser().parse(_game.toString()).getAsJsonObject();
        JsonObject gameServer = json.getAsJsonObject("GameServer");
        int grade = gameServer.get("grade").getAsInt();
        return grade;
    }

    private void drawBackGround(Graphics g) {
        int r = 300;
        Graphics2D g2 = (Graphics2D) g;
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image backGround = tk.getImage("data\\images\\backgroundsecound.png");
        g2.drawImage(backGround, 0,0, 1550, 840, this);
    }

    private void updateFrame() {
        Range rx = new Range(20, this.getWidth() - 20);
        Range ry = new Range(this.getHeight() - 10, 150);
        Range2D frame = new Range2D(rx, ry);
        directed_weighted_graph g = _ar.getGraph();
        _w2f = Arena.w2f(g, frame); //Receive a Range2Range object that is built of 2 Range2D's:
        //Range of graph and Range of frame.
    }

    /**
     * This method add to make the frame running clean.
     * Token from: https://www.youtube.com/watch?v=4T3WJEH7zrc&feature=youtu.be
     *
     * @param g-graphic.
     */
    public void paint(Graphics g) {
        dbImage = createImage(getWidth(), getHeight());
        dbGraphic = dbImage.getGraphics();
        paintComponent(dbGraphic);
        g.drawImage(dbImage, 0, 0, this);
    }

    public void paintComponent(Graphics g) {
        int w = this.getWidth();
        int h = this.getHeight();
        g.clearRect(0, 0, w, h);
        drawBackGround(g);
        drawPokemons(g);
        drawGraph(g);
        drawAgents(g);
        drawInfo(g);
        drawPanel(g);

    }

    private void drawInfo(Graphics g) {
        List<String> str = _ar.get_info();
        String dt = "none";
        for (int i = 0; i < str.size(); i++) {
            g.drawString(str.get(i) + " dt: " + dt, 100, 60 + i * 20);
        }

    }

    private void drawGraph(Graphics g) {
        directed_weighted_graph gg = _ar.getGraph();
        Iterator<node_data> iter = gg.getV().iterator();
        while (iter.hasNext()) {
            node_data n = iter.next();
            g.setColor(Color.blue);
            drawNode(n, 5, g);
            Iterator<edge_data> itr = gg.getE(n.getKey()).iterator();
            while (itr.hasNext()) {
                edge_data e = itr.next();
                g.setColor(Color.gray);
                drawEdge(e, g);
            }
        }
    }

    private void drawPokemons(Graphics g) {
        List<Pokemon> fs = _ar.getPokemons();
        if (fs != null) {
            Iterator<Pokemon> itr = fs.iterator();

            while (itr.hasNext()) {

                Pokemon f = itr.next();
                geo_location c = f.getLocation();
                int r = 30;
                Graphics2D g2 = (Graphics2D) g;
                Toolkit tk = Toolkit.getDefaultToolkit();
                Image lowValue = tk.getImage( "data\\images\\lowValue.jpg");
                Image mediumValue = tk.getImage("data\\images\\mediumValue.jpg");
                Image picatchu = tk.getImage("data\\images\\picatchu.jpg");
                Image highValue = tk.getImage("data\\images\\highvalue.jpg");
                Image img = lowValue;
                if (f.getValue() <= 5)
                    img = lowValue;
                if (f.getValue() > 5 && f.getValue() <= 10)
                    img = mediumValue;
                if (f.getValue() > 10&& f.getValue()<15)
                    img=picatchu;
                if(f.getValue()>=15)
                    img = highValue;
                if (c != null) {
                    geo_location fp = this._w2f.world2frame(c);
                    g.setFont(new Font("",Font.BOLD,15));
                    g.setColor(Color.BLACK);
                    g2.drawString("value: " + f.getValue(), (int) fp.x()-40, (int) fp.y() +45);
                    g2.drawImage(img, (int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r, this);
                }
            }
        }
    }

    private void drawAgents(Graphics g) {
        List<Agent> rs = _ar.getAgents();
        //	Iterator<OOP_Point3D> itr = rs.iterator();
        g.setColor(Color.red);
        int i = 0;
        while (rs != null && i < rs.size()) {
            geo_location c = rs.get(i).getLocation();
            int r = 20;
            Graphics2D g2 = (Graphics2D) g;
            Toolkit tk = Toolkit.getDefaultToolkit();
            Image agent = tk.getImage("data\\images\\agent.jpg");
            i++;
            if (c != null) {

                geo_location fp = this._w2f.world2frame(c);
                g2.drawImage(agent, (int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r, this);
            }
        }
    }

    private void drawNode(node_data n, int r, Graphics g) {
        geo_location pos = n.getLocation();
        geo_location fp = this._w2f.world2frame(pos);
        g.fillOval((int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r);
        g.drawString("" + n.getKey(), (int) fp.x(), (int) fp.y() - 4 * r);
    }

    private void drawEdge(edge_data e, Graphics g) {
        directed_weighted_graph gg = _ar.getGraph();
        geo_location s = gg.getNode(e.getSrc()).getLocation();
        geo_location d = gg.getNode(e.getDest()).getLocation();
        geo_location s0 = this._w2f.world2frame(s);
        geo_location d0 = this._w2f.world2frame(d);
        g.setColor(Color.BLACK);
        g.drawString("" + (int) e.getWeight(), (int) (s0.x()+d0.x())/2, (int) (s0.y()+d0.y())/2);
        g.drawLine((int) s0.x(), (int) s0.y(), (int) d0.x(), (int) d0.y());
    }
}