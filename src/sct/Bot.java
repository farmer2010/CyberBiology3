package sct;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import java.util.ListIterator;

public class Bot{
	ArrayList<Bot> objects;
	Random rand = new Random();
	private int x;
	private int y;
	public int xpos;
	public int ypos;
	public Color color;
	public int energy;
	public int minerals;
	public int killed = 0;
	public int[][] map;
	public int[][][] commands = new int[5][15][8];
	private int index = 0;
	public int age = 1000;
	public int state = 0;//бот или органика
	public int state2 = 1;//что ставить в массив с миром
	private int rotate = rand.nextInt(8);
	private int[][] movelist = {
		{0, -1},
		{1, -1},
		{1, 0},
		{1, 1},
		{0, 1},
		{-1, 1},
		{-1, 0},
		{-1, -1}
	};
	private int[] minerals_list = {
		1,
		2,
		3
	};
	private int[] photo_list = {
		10,
		8,
		6,
		5,
		4,
		3
	};
	private int[] world_scale = {162, 108};
	private int c_red = 0;
	private int c_green = 0;
	private int c_blue = 0;
	private int sector_len = world_scale[1] / 8;
	public boolean[] genes = {
		boolrand(),
		boolrand(),
		boolrand(),
		boolrand()
	};
	private boolean is_attacked = false;
	private int memory = 0;
	public Bot(int new_xpos, int new_ypos, Color new_color, int new_energy, int[][] new_map, ArrayList<Bot> new_objects) {
		xpos = new_xpos;
		ypos = new_ypos;
		x = new_xpos * 10;
		y = new_ypos * 10;
		color = new_color;
		energy = new_energy;
		minerals = 0;
		objects = new_objects;
		map = new_map;
		for (int drx = 0; drx < 5; drx++) {
			for (int dry = 0; dry < 15; dry++) {
				commands[drx][dry][0] = rand.nextInt(25);
				commands[drx][dry][1] = rand.nextInt(25);
				commands[drx][dry][2] = rand.nextInt(64);
				commands[drx][dry][3] = rand.nextInt(64);
				commands[drx][dry][4] = rand.nextInt(64);
				commands[drx][dry][5] = rand.nextInt(64);
				commands[drx][dry][6] = rand.nextInt(46);
				commands[drx][dry][7] = rand.nextInt(64);
			}
		}
		//world_scale[0] = map.length;
		//world_scale[1] = map[0].length;
	}
	public void Draw(Graphics canvas, int draw_type) {
		if (state == 0) {//рисуем бота
			canvas.setColor(new Color(0, 0, 0));
			canvas.fillRect(x, y, 10, 10);
			if (draw_type == 0) {//режим отрисовки хищников
				int r = 0;
				int g = 0;
				int b = 0;
				if (c_red + c_green + c_blue == 0) {
					r = 128;
					g = 128;
					b = 128;
				}else {
					r = (int)((c_red * 1.0) / (c_red + c_green + c_blue) * 255.0);
					g = (int)((c_green * 1.0) / (c_red + c_green + c_blue) * 255.0);
					b = (int)((c_blue * 1.0) / (c_red + c_green + c_blue) * 255.0);
				}
				canvas.setColor(new Color(r, g, b));
			}else if (draw_type == 1) {//цвета
				canvas.setColor(color);
			}else if (draw_type == 2) {//энергии
				int g = 255 - (int)(energy / 1000.0 * 255.0);
				if (g > 255) {
					g = 255;
				}else if (g < 0) {
					g = 0;
				}
				canvas.setColor(new Color(255, g, 0));
			}else if (draw_type == 3) {//минералов
				int rg = 255 - (int)(minerals / 1000.0 * 255.0);
				if (rg > 255) {
					rg = 255;
				}else if (rg < 0) {
					rg = 0;
				}
				canvas.setColor(new Color(rg, rg, 255));
			}else if (draw_type == 4) {//возраста
				canvas.setColor(new Color((int)(age / 1000.0 * 255.0), (int)(age / 1000.0 * 255.0), (int)(age / 1000.0 * 255.0)));
			}else if (draw_type == 5) {//генов
				canvas.setColor(new Color(0, genes_to_number() * 16, 0));
			}
			canvas.fillRect(x + 1, y + 1, 8, 8);
		}else {//рисуем органику
			canvas.setColor(new Color(0, 0, 0));
			canvas.fillRect(x + 1, y + 1, 8, 8);
			canvas.setColor(new Color(128, 128, 128));
			canvas.fillRect(x + 2, y + 2, 6, 6);
		}
	}
	public int Update(ListIterator<Bot> iterator) {
		if (killed == 0) {
			if (state == 0) {//бот
				int sector = bot_in_sector();
				energy -= 1;
				age -= 1;
				if (sector <= 7 & sector >= 5) {
					minerals += minerals_list[sector - 5];
				}
				update_commands(iterator);
				is_attacked = false;
				if (energy <= 0) {
					killed = 1;
					map[xpos][ypos] = 0;
					return(0);
				}else if (energy > 1000) {
					energy = 1000;
				}
				if (age <= 0) {
					state = 1;
					state2 = 2;
					map[xpos][ypos] = 2;
					return(0);
				}
				if (minerals > 1000) {
					minerals = 1000;
				}
			}else if (state == 1) {//падающая органика
				move(4);
				int[] pos = get_rotate_position(4);
				if (pos[1] > 0 & pos[1] < world_scale[1]) {
					if (map[pos[0]][pos[1]] != 0) {
						state = 2;
					}
				}
			}else {//стоящая органика
				//
			}
		}
		return(0);
	}
	public void update_commands(ListIterator<Bot> iterator) {//мозг
		boolean[] check = {
			is_attacked,
			energy < 100,
			age < 100,
			energy > 900,
			age > 900,
			true
		};
		int dry;
		for (dry = 0; dry < 6; dry++) {
			if (check[dry]) {
				if (dry != 0 && dry != 5) {
					if (genes[dry - 1]) {
						break;
					}
				}else {
					break;
				}
			}
		}
		if (dry == 5) {
			dry += index;
		}
		int drx = see(rotate);
		int[] cmd = commands[drx][dry];
		int[] n = {cmd[6], cmd[7]};
		int command;
		int param1;
		int param2;
		if (condition(n)) {
			command = cmd[0];
			param1 = cmd[2];
			param2 = cmd[3];
		}else {
			command = cmd[1];
			param1 = cmd[4];
			param2 = cmd[5];
		}
		if (command == 0 || command == 12) {//фотосинтез
			if (bot_in_sector() <= 5) {
				energy += photo_list[bot_in_sector()];
				c_green++;
			}
		}else if (command == 1) {//повернуть направо
			rotate += 1;
			if (rotate > 7) {
				rotate = 0;
			}
		}else if (command == 2) {//повенуть налево
			rotate -= 1;
			if (rotate < 0) {
				rotate = 7;
			}
		}else if (command == 3 || command == 13) {//походить
			move(rotate);
			energy--;
		}else if (command == 4 || command == 14) {//атаковать
			attack(rotate);
		}else if (command == 5 || command == 15) {//поделиться
			multiply(rotate, iterator);
		}else if (command == 6 || command == 16) {//преобразовать минералы в энергию
			if (minerals > 0) {
				c_blue++;
			}
			int mnr = 50;
			if (minerals < 50) {
				mnr = minerals;
			}
			minerals -= mnr;
			energy += mnr * 2;
		}else if (command == 7) {//мутировать соседа
			mutate_neighbour(rotate);
		}else if (command == 8 || command == 17) {//отдать часть ресурсов
			give(rotate);
		}else if (command == 9 || command == 18) {//равномерное распределение ресурсов
			give2(rotate);
		}else if (command == 10) {//записать число в память
			if (param1 % 9 == 0) {
				memory = param2;
			}else if (param1 % 9 == 1) {
				memory = drx * 15;
			}else if (param1 % 9 == 2) {
				memory -= param2;
				if (memory < 0) {
					memory = 0;
				}
			}else if (param1 % 9 == 3) {
				memory += param2;
				memory %= 64;
			}else if (param1 % 9 == 4) {
				memory = (int)(age / 1000.0 * 63);
			}else if (param1 % 9 == 5) {
				memory = (int)(minerals / 1000.0 * 63);
			}else if (param1 % 9 == 6) {
				memory = (int)(energy / 1000.0 * 63);
			}else if (param1 % 9 == 7) {
				memory = (int)(xpos * 1.0 / world_scale[0] * 63);
			}else if (param1 % 9 == 8) {
				memory = (int)(ypos * 1.0 / world_scale[1] * 63);
			}
		}else if (command == 11) {//повернуть в направлении
			rotate = param1 % 8;
		}else if (command == 19) {//мутировать
			mutate_command(commands[rand.nextInt(5)][rand.nextInt(15)]);
		}else if (command == 20) {//походить относительно
			move((rotate + param1) % 8);
			energy--;
		}else if (command == 21) {//атаковать относительно
			attack((rotate + param1) % 8);
		}else if (command == 22) {//поделиться отноительно
			multiply((rotate + param1) % 8, iterator);
		}else if (command == 23) {//отдать часть ресурсов относительно
			give2((rotate + param1) % 8);
		}else if (command == 24) {//равномерное распределение ресурсов относительно
			give((rotate + param1) % 8);
		}
		if (dry >= 5) {
			index++;
			if (index > 9) {
				index = 0;
			}
		}
	}
	public void mutate_neighbour(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 1) {
				Bot neighbour = find(pos);
				if (neighbour != null) {
					neighbour.mutate_command(neighbour.commands[rand.nextInt(5)][rand.nextInt(15)]);
				}
			}
		}
	}
	public void give(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 1) {
				Bot relative = find(pos);
				if (relative.killed == 0 && relative.state == 0) {
					relative.energy += energy / 4;
					relative.minerals += minerals / 4;
					energy -= energy / 4;
					minerals -= minerals / 4;
				}
			}
		}
	}
	public void give2(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 1) {
				Bot relative = find(pos);
				if (relative.killed == 0 && relative.state == 0) {
					int enr = relative.energy + energy;
					int mnr = relative.minerals + minerals;
					relative.energy = enr / 2;
					relative.minerals = mnr / 2;
					energy = enr / 2;
					minerals = mnr / 2;
				}
			}
		}
	}
	public void attack(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] != 0) {
				Bot victim = find(pos);
				if (victim != null) {
					if (victim.energy >= 150) {
						energy += 150;
						victim.is_attacked = true;
						victim.energy -= 150;
						c_red++;
					}else {
						energy += victim.energy;
						if (victim.energy != 0) {
							c_red++;
						}
						victim.energy = 0;
						victim.killed = 1;
						map[pos[0]][pos[1]] = 0;
					}
				}
			}
		}
	}
	public void attack2(int rot) {//атаковать
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] != 0) {
				Bot victim = find(pos);
				if (victim != null) {
					energy += victim.energy;
					victim.energy = 0;
					victim.killed = 1;
					map[pos[0]][pos[1]] = 0;
					c_red++;
				}
			}
		}
	}
	public Bot find(int[] pos) {//только если есть сосед
		for (Bot b: objects) {
			if (b.killed == 0 & b.xpos == pos[0] & b.ypos == pos[1]) {
				return(b);
			}
		}
		return(null);
	}
	public boolean is_relative(int[][][] brain1, int[][][] brain2) {
		int errors = 0;
		for (int drx = 0; drx < 5; drx++) {
			for (int dry = 0; dry < 15; dry++) {
				for (int drz = 0; drz < 8; drz++) {
					if (brain1[drx][dry][drz] != brain2[drx][dry][drz]) {
						errors += 1;
					}
					if (errors > 1) {
						return(false);
					}
				}
			}
		}
		return(errors < 2);
	}
	public int[] get_rotate_position(int rot){
		int[] pos = new int[2];
		pos[0] = (xpos + movelist[rot][0]) % world_scale[0];
		pos[1] = ypos + movelist[rot][1];
		if (pos[0] < 0) {
			pos[0] = 161;
		}else if(pos[0] >= world_scale[0]) {
			pos[0] = 0;
		}
		return(pos);
	}
	public int move(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 0) {
				map[xpos][ypos] = 0;
				xpos = pos[0];
				ypos = pos[1];
				x = xpos * 10;
				y = ypos * 10;
				map[xpos][ypos] = state2;
				return(1);
			}
		}
		return(0);
	}
	public void multiply(int rot, ListIterator<Bot> iterator) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 0) {
				energy -= 150;
				if (energy <= 0) {
					killed = 1;
					map[xpos][ypos] = 0;
				}else {
					map[pos[0]][pos[1]] = 1; 
					Color new_color = color;
					boolean[] new_genes = new boolean[4];
					for (int i = 0; i < 4; i++) {
						new_genes[i] = genes[i];
					}
					int[][][] new_brain = new int[5][15][8];
					for (int drx = 0; drx < 5; drx++) {
						for (int dry = 0; dry < 15; dry++) {
							for (int drz = 0; drz < 8; drz++) {
								new_brain[drx][dry][drz] = commands[drx][dry][drz];
							}
						}
					}
					if (rand.nextInt(4) == 0) {//мутация
						new_color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
						mutate_command(new_brain[rand.nextInt(5)][rand.nextInt(15)]);
						int r = rand.nextInt(4);
						new_genes[r] = !new_genes[r];
					}
					Bot new_bot = new Bot(pos[0], pos[1], new_color, energy / 2, map, objects);
					new_bot.minerals = minerals / 2;
					energy /= 2;
					minerals /= 2;
					new_bot.commands = new_brain;
					new_bot.genes = new_genes;
					iterator.add(new_bot);
				}
			}
		}
	}
	public int see(int rot) {
		int[] pos = get_rotate_position(rotate);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 0) {
				return(1);//если ничего
			}else if (map[pos[0]][pos[1]] == 1) {
				Bot b = find(pos);
				if (b != null) {
					if (is_relative(commands, b.commands)) {
						return(3);//если родственник
					}else {
						return(2);//если враг
					}
				}else {
					return(1);//если ничего
				}
			}else if (map[pos[0]][pos[1]] == 2) {
				//System.out.println(1);
				return(4);//если органика
			}
		}else {
			return(0);//если граница
		}
		return(1);
	}
	public int bot_in_sector() {
		int sec = ypos / sector_len;
		if (sec > 7) {
			sec = 10;
		}
		return(sec);
	}
	public boolean boolrand() {
		return(rand.nextInt(2) == 1);
	}
	public void mutate_command(int[] command) {
		int ind = rand.nextInt(8);
		if (ind <= 1) {
			command[ind] = rand.nextInt(25);
		}else if (ind == 6) {
			command[ind] = rand.nextInt(46);
		}else {
			command[ind] = rand.nextInt(64);
		}
	}
	public boolean condition(int[] command) {
		int cmd = command[0];
		if (cmd > 22) {
			return(true);
		}else {
			if (cmd == 0) {//есть ли фотосинтез
				return(bot_in_sector() <= 5);
			}else if (cmd == 1) {//есть ли приход минералов
				return(bot_in_sector() <= 7 && bot_in_sector() >= 5);
			}else if (cmd == 2) {//направление равно параметру
				return(rotate == command[1] % 8);
			}else if (cmd == 3) {//направление больше параметра
				return(rotate > command[1] % 8);
			}else if (cmd == 4) {//направление меньше параметра
				return(rotate < command[1] % 8);
			}else if (cmd == 5) {//сколько минералов
				return((int)(minerals / 1000.0 * 63) == command[1]);
			}else if (cmd == 6) {//минералов больше параметра
				return((int)(minerals / 1000.0 * 63) > command[1]);
			}else if (cmd == 7) {//минералов меньше параметра
				return((int)(minerals / 1000.0 * 63) < command[1]);
			}else if (cmd == 8) {//память равна параметру
				return(memory == command[1]);
			}else if (cmd == 9) {//память больше параметра
				return(memory > command[1]);
			}else if (cmd == 10) {//память маньше параметра
				return(memory < command[1]);
			}else if (cmd == 11) {//какая позиция(х)
				return((int)(xpos * 1.0 / world_scale[0] * 63) == command[1]);
			}else if (cmd == 12) {//позиция(х) больше параметра
				return((int)(xpos * 1.0 / world_scale[0] * 63) > command[1]);
			}else if (cmd == 13) {//позиция(х) меньше параметра
				return((int)(xpos * 1.0 / world_scale[0] * 63) < command[1]);
			}else if (cmd == 14) {//какая позиция(у)
				return((int)(ypos * 1.0 / world_scale[1] * 63) == command[1]);
			}else if (cmd == 15) {//позиция(у) больше параметра
				return((int)(ypos * 1.0 / world_scale[1] * 63) > command[1]);
			}else if (cmd == 16) {//позиция(у) меньше параметра
				return((int)(ypos * 1.0 / world_scale[1] * 63) < command[1]);
			}else if (cmd == 17) {//сколько энергии
				return((int)(energy / 1000.0 * 63) == command[1]);
			}else if (cmd == 18) {//энергии больше параметра
				return((int)(energy / 1000.0 * 63) > command[1]);
			}else if (cmd == 19) {//энергии меньше параметра
				return((int)(energy / 1000.0 * 63) < command[1]);
			}else if (cmd == 20) {//какой возраст
				return((int)(age / 1000.0 * 63) == command[1]);
			}else if (cmd == 21) {//возраст больше параметра
				return((int)(age / 1000.0 * 63) > command[1]);
			}else if (cmd == 22) {//возраст меньше параметра
				return((int)(age / 1000.0 * 63) < command[1]);
			}
			return(false);
		}
	}
	public int genes_to_number() {
		int ret = 0;
		for (int i = 0; i < 4; i++) {
			if (genes[i]) {
				ret += Math.pow(2, i);
			}
		}
		return(ret);
	}
}
