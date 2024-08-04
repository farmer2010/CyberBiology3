package sct;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import javax.swing.*;
import java.awt.Font;
//
import java.io.File;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Rectangle;
import java.io.IOException;
import java.awt.AWTException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import javax.swing.filechooser.FileSystemView;
import java.awt.Graphics2D;

public class World extends JPanel{
	ArrayList<Bot> objects;
	int size = 25;
	Timer timer;
	int delay = 10;
	Random rand = new Random();
	int[][] Map = new int[162][108];//0 - none, 1 - bot, 2 - organics
	Color gray = new Color(100, 100, 100);
	Color green = new Color(0, 255, 0);
	Color red = new Color(255, 0, 0);
	Color black = new Color(0, 0, 0);
	int steps = 0;
	int draw_type = 0;
	int b_count = 0;
	int obj_count = 0;
	int org_count = 0;
	String txt;
	String txt2;
	int mouse = 0;
	int W = 1920;
	int H = 1080;
	JButton stop_button = new JButton("Stop");
	boolean pause = false;
	boolean render = true;
	Bot selection = null;
	int[] botpos = new int [2];
	Bot for_set = null;
	JButton save_button = new JButton("Save");
	JButton show_brain_button = new JButton("Show brain");
	JButton render_button = new JButton("Render: on");
	JButton record_button = new JButton("Record: off");
	boolean sh_brain = false;
	boolean rec = false;
	public World() {
		setLayout(null);
		timer = new Timer(delay, new BotListener());
		objects = new ArrayList<Bot>();
		setBackground(new Color(255, 255, 255));
		addMouseListener(new BotListener());
		addMouseMotionListener(new BotListener());
		stop_button.addActionListener(new start_stop());
		stop_button.setBounds(W - 300, 125, 250, 35);
        add(stop_button);
        JButton predators_button = new JButton("Predators");
        predators_button.addActionListener(new dr1());
		predators_button.setBounds(W - 300, 190, 125, 20);
        add(predators_button);
        JButton energy_button = new JButton("Energy");
        energy_button.addActionListener(new dr3());
		energy_button.setBounds(W - 170, 190, 125, 20);
        add(energy_button);
        JButton minerals_button = new JButton("Minerals");
		minerals_button.setBounds(W - 300, 215, 125, 20);
		minerals_button.addActionListener(new dr4());
        add(minerals_button);
        JButton age_button = new JButton("Age");
        age_button.addActionListener(new dr5());
		age_button.setBounds(W - 170, 215, 125, 20);
        add(age_button);
        JButton color_button = new JButton("Color");
        color_button.addActionListener(new dr2());
		color_button.setBounds(W - 300, 240, 125, 20);
        add(color_button);
        JButton select_button = new JButton("Select");
        select_button.addActionListener(new select());
		select_button.setBounds(W - 300, 455, 95, 20);
        add(select_button);
        JButton set_button = new JButton("Set");
        set_button.addActionListener(new set());
        set_button.setBounds(W - 200, 455, 95, 20);
        add(set_button);
        JButton remove_button = new JButton("Remove");
        remove_button.addActionListener(new remove());
        remove_button.setBounds(W - 100, 455, 95, 20);
        add(remove_button);
        //
        //save_button.addActionListener(new remove());
        save_button.setBounds(W - 300, 365, 125, 20);
        save_button.setEnabled(false);
        add(save_button);
        //
        show_brain_button.addActionListener(new shbr());
        show_brain_button.setBounds(W - 170, 365, 125, 20);
        show_brain_button.setEnabled(false);
        add(show_brain_button);
        JTextField for_save = new JTextField();
        for_save.setBounds(W - 300, 410, 250, 20);
        add(for_save);
        JTextField for_load = new JTextField();
        for_load.setBounds(W - 300, 515, 250, 20);
        add(for_load);
        JButton load_bot_button = new JButton("Load bot");
        //load_bot_button.addActionListener(new remove());
        load_bot_button.setBounds(W - 300, 540, 125, 20);
        add(load_bot_button);
        JButton load_world_button = new JButton("Load world");
        //load_world_button.addActionListener(new remove());
        load_world_button.setBounds(W - 170, 540, 125, 20);
        add(load_world_button);
        JButton new_population_button = new JButton("New population");
        new_population_button.addActionListener(new nwp());
        new_population_button.setBounds(W - 300, 590, 125, 20);
        add(new_population_button);
        //
        render_button.addActionListener(new rndr());
        render_button.setBounds(W - 300, 615, 125, 20);
        add(render_button);
        //
        record_button.addActionListener(new rcrd());
        record_button.setBounds(W - 170, 615, 125, 20);
        add(record_button);
        JButton kill_button = new JButton("Kill all");
        kill_button.addActionListener(new kill_all());
        kill_button.setBounds(W - 170, 590, 125, 20);
        add(kill_button);
		//newPopulation();
		timer.start();
	}
	public void paintComponent(Graphics canvas) {
		super.paintComponent(canvas);
		//for (int x = 0; x < 162; x++) {
		//	for (int y = 0; y < 108; y++) {
		//		if (Map[x][y] == 1) {
		//			canvas.setColor(green);
		//			canvas.fillRect(x * 10, y * 10, 10, 10);
		//		}else if (Map[x][y] == 2){
		//			canvas.setColor(red);
		//			canvas.fillRect(x * 10, y * 10, 10, 10);
		//		}
		//	}
		//}
		if (render) {
			for(Bot b: objects) {
				b.Draw(canvas, draw_type);
			}
		}
		canvas.setColor(gray);
		canvas.fillRect(W - 300, 0, 300, 1080);
		canvas.setColor(black);
		canvas.setFont(new Font("arial", Font.BOLD, 18));
		canvas.drawString("Main: ", W - 300, 20);
		canvas.drawString("version 1.0", W - 300, 40);
		canvas.drawString("steps: " + String.valueOf(steps), W - 300, 60);
		canvas.drawString("objects: " + String.valueOf(obj_count) + ", bots: " + String.valueOf(b_count), W - 300, 80);
		if (draw_type == 0) {
			txt = "predators view";
		}else if (draw_type == 1) {
			txt = "color view";
		}else if (draw_type == 2) {
			txt = "energy view";
		}else if (draw_type == 3) {
			txt = "minerals view";
		}else if (draw_type == 4){
			txt = "age view";
		}else if (draw_type == 5) {
			txt = "genes view";
		}
		canvas.drawString("render type: " + txt, W - 300, 100);
		if (mouse == 0) {
			txt2 = "select";
		}else if (mouse == 1) {
			txt2 = "set";
		}else {
			txt2 = "remove";
		}
		canvas.drawString("mouse function: " + txt2, W - 300, 120);
		canvas.drawString("Render types:", W - 300, 180);
		canvas.drawString("Selection:", W - 300, 275);
		canvas.drawString("enter name:", W - 300, 405);
		canvas.drawString("Mouse functions:", W - 300, 445);
		canvas.drawString("Load:", W - 300, 490);
		canvas.drawString("enter name:", W - 300, 510);
		canvas.drawString("Controls:", W - 300, 580);
		if (selection != null) {
			canvas.drawString("energy: " + String.valueOf(selection.energy) + ", minerals: " + String.valueOf(selection.minerals), W - 300, 295);
			canvas.drawString("age: " + String.valueOf(selection.age), W - 300, 315);
			canvas.drawString("position: " + "[" + String.valueOf(selection.xpos) + ", " + String.valueOf(selection.ypos) + "]", W - 300, 335);
			canvas.drawString("color: " + "(" + String.valueOf(selection.color.getRed()) + ", " + String.valueOf(selection.color.getGreen()) + ", " + String.valueOf(selection.color.getBlue()) + ")", W - 300, 355);
			canvas.setColor(new Color(90, 90, 90, 90));
			canvas.fillRect(0, 0, W - 300, 1080);
			canvas.setColor(new Color(255, 0, 0));
			canvas.fillRect(selection.xpos * 10, selection.ypos * 10, 10, 10);
		}else {
			canvas.drawString("none", W - 300, 295);
		}
		if (sh_brain) {
			canvas.setColor(new Color(90, 90, 90));
			canvas.fillRect(0, 0, 360, 360);
			canvas.setColor(new Color(128, 128, 128));
			for (int x = 0; x < 5; x++) {
				for (int y = 0; y < 6; y++) {
					canvas.setColor(new Color(128, 128, 128));
					canvas.fillRect(x * 45, y * 45, 40, 40);
					canvas.setColor(new Color(0, 0, 0));
					canvas.drawString(String.valueOf(selection.commands[x][y]), x * 45 + 20, y * 45 + 20);
				}
			}
		}
		if (rec && steps % 25 == 0) {
			try {
				int last = draw_type;
				draw_type = 0;
				BufferedImage buff = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
				Graphics2D g2d = buff.createGraphics();
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, 1920, 1080);
				for(Bot b: objects) {
					b.Draw(g2d, draw_type);
				}
				g2d.dispose();
				draw_type = 2;
				BufferedImage buff2 = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
				g2d = buff2.createGraphics();
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, 1920, 1080);
				for(Bot b: objects) {
					b.Draw(g2d, draw_type);
				}
				g2d.dispose();
				draw_type = 5;
				BufferedImage buff3 = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
				g2d = buff3.createGraphics();
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, 1920, 1080);
				for(Bot b: objects) {
					b.Draw(g2d, draw_type);
				}
				g2d.dispose();
				draw_type = last;
				ImageIO.write(buff, "png", new File("record/predators/screen" + String.valueOf(steps / 25)+ ".png"));
				ImageIO.write(buff2, "png", new File("record/energy/screen" + String.valueOf(steps / 25)+ ".png"));
				ImageIO.write(buff3, "png", new File("record/color/screen" + String.valueOf(steps / 25)+ ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void newPopulation() {
		steps = 0;
		objects = new ArrayList<Bot>();
		Map = new int[162][108];//0 - none, 1 - bot, 2 - organics
		for (int i = 0; i < 1000; i++) {
			while(true){
				int x = rand.nextInt(162);
				int y = rand.nextInt(108);
				if (Map[x][y] == 0) {
					objects.add(new Bot(
						x,
						y,
						new Color(rand.nextInt(256),rand.nextInt(256), rand.nextInt(256)),
						1000,
						Map,
						objects
					));
					Map[x][y] = 1;
					break;
				}
			}
		}
		repaint();
	}
	private class BotListener extends MouseAdapter implements ActionListener{
		public void mousePressed(MouseEvent e) {
			if (e.getX() < W - 300) {
				botpos[0] = e.getX() / 10;
				botpos[1] = e.getY() / 10;
				if (mouse == 0) {//select
					if (Map[botpos[0]][botpos[1]] == 1) {
						for(Bot b: objects) {
							if (b.xpos == botpos[0] && b.ypos == botpos[1]) {
								selection = b;
								save_button.setEnabled(true);
								show_brain_button.setEnabled(true);
							}
						}
					}else {
						selection = null;
						save_button.setEnabled(false);
						show_brain_button.setEnabled(false);
						sh_brain = false;
					}
				}else if (mouse == 1) {//set
					if (for_set != null) {
						if (Map[botpos[0]][botpos[1]] == 0) {
							objects.add(for_set);
							Map[botpos[0]][botpos[1]] = for_set.state2;
						}
					}
				}else {//remove
					if (Map[botpos[0]][botpos[1]] != 0) {
						for(Bot b: objects) {
							if (b.xpos == botpos[0] && b.ypos == botpos[1]) {
								b.energy = 0;
								b.killed = 1;
								Map[botpos[0]][botpos[1]] = 0;
							}
						}
					}
				}
			}
		}
		public void mouseDragged(MouseEvent e) {
			if (e.getX() < W - 300) {
				botpos[0] = e.getX() / 10;
				botpos[1] = e.getY() / 10;
				if (mouse == 1) {//set
					//
				}else if (mouse == 2) {//remove
					if (Map[botpos[0]][botpos[1]] != 0) {
						for(Bot b: objects) {
							if (b.xpos == botpos[0] && b.ypos == botpos[1]) {
								b.energy = 0;
								b.killed = 1;
								Map[botpos[0]][botpos[1]] = 0;
							}
						}
					}
				}
			}
		}
		public void actionPerformed(ActionEvent e) {
			if (!pause) {
				steps++;
				b_count = 0;
				obj_count = 0;
				org_count = 0;
				ListIterator<Bot> bot_iterator = objects.listIterator();
				while (bot_iterator.hasNext()) {
					Bot next_bot = bot_iterator.next();
					next_bot.Update(bot_iterator);
					if (selection != null) {
						if (next_bot.xpos == selection.xpos && next_bot.ypos == selection.ypos) {
							if (next_bot != selection) {
								selection = null;
								save_button.setEnabled(false);
								show_brain_button.setEnabled(false);
								sh_brain = false;
							}
						}
					}
					obj_count++;
					if (next_bot.state != 0) {
						org_count++;
					}else {
						b_count++;
					}
				}
				if (selection != null) {
					if (selection.killed == 1 || Map[selection.xpos][selection.ypos] != 1 || selection.state != 0){
						selection = null;
						save_button.setEnabled(false);
						show_brain_button.setEnabled(false);
						sh_brain = false;
					}
				}
			}
			ListIterator<Bot> iterator = objects.listIterator();
			while (iterator.hasNext()) {
				Bot next_bot = iterator.next();
				if (next_bot.killed == 1) {
					iterator.remove();
				}
			}
			repaint();
			
		}
		
	}
	private class dr1 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 0;
		}
	}
	private class dr2 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 1;
		}
	}
	private class dr3 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 2;
		}
	}
	private class dr4 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 3;
		}
	}
	private class dr5 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 4;
		}
	}
	private class start_stop implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			pause = !pause;
			if (pause) {
				stop_button.setText("Start");
			}else {
				stop_button.setText("Stop");
			}
		}
	}
	private class select implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			mouse = 0;
		}
	}
	private class set implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			mouse = 1;
		}
	}
	private class remove implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			mouse = 2;
		}
	}
	private class nwp implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			newPopulation();
		}
	}
	private class rndr implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			render = !render;
			if (render) {
				render_button.setText("Render: on");
			}else {
				render_button.setText("Render: off");
			}
		}
	}
	private class rcrd implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			rec = !rec;
			if (rec) {
				record_button.setText("Record: on");
			}else {
				record_button.setText("Record: off");
			}
		}
	}
	private class shbr implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			sh_brain = !sh_brain;
			if (pause == false) {
				pause = true;
			}else if (sh_brain == false) {
				pause = false;
			}
		}
	}
	private class kill_all implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			steps = 0;
			objects = new ArrayList<Bot>();
			Map = new int[162][108];//0 - none, 1 - bot, 2 - organics
		}
	}
}
