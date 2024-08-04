def condition(self, command):#обработка условий
    #command = [условие, аргумент]
    cmd = command[0]
    if cmd > 22:#условия 0 - 22(если нет условия)
        return 1
    else:#если есть условие
        if cmd == 0:#есть ли приход энергии(от фотосинтеза)
            sector = self.bot_in_sector()
            return sector <= 5
        elif cmd == 1:#есть ли приход минералов
            sector = self.bot_in_sector()
            return sector <= 7 and sector >= 5
        elif cmd == 2:#какое мое направление
            return self.rotate == (command[1] % 8)
        elif cmd == 3:#мое направление больше аргумента
            return self.rotate > (command[1] % 8)
        elif cmd == 4:#мое направление меньше аргумента
            return self.rotate < (command[1] % 8)
        elif cmd == 5:#сколько у меня минералов
            return int(self.minerals / 1000 * 63) == command[1]
        elif cmd == 6:#минералов больше аргумента
            return int(self.minerals / 1000 * 63) > command[1]
        elif cmd == 7:#минералов меньше аргумента
            return int(self.minerals / 1000 * 63) < command[1]
        elif cmd == 8:#память равна аргументу
            return self.variable == command[1]
        elif cmd == 9:#память больше аргумента
            return self.variable > command[1]
        elif cmd == 10:#память меньше аргумента
            return self.variable < command[1]
        elif cmd == 11:#какая моя позиция (x)
            return int(self.pos[0] / self.world_scale[0] * 63) == command[1]
        elif cmd == 12:#моя позиция (x) больше аргумента
            return int(self.pos[0] / self.world_scale[0] * 63) > command[1]
        elif cmd == 13:#моя позиция (x) меньше аргумента
            return int(self.pos[0] / self.world_scale[0] * 63) < command[1]
        elif cmd == 14:#какая моя позиция (y)
            return int(self.pos[1] / self.world_scale[1] * 63) == command[1]
        elif cmd == 15:#моя позиция (y) больше аргумента
            return int(self.pos[1] / self.world_scale[1] * 63) > command[1]
        elif cmd == 16:#моя позиция (y) меньше аргумента
            return int(self.pos[1] / self.world_scale[1] * 63) < command[1]
        elif cmd == 17:#сколько у меня энергии
            return int(self.energy / 1000 * 63) == command[1]
        elif cmd == 18:#энергии больше аргумента
            return int(self.energy / 1000 * 63) > command[1]
        elif cmd == 19:#энергии меньше аргумента
            return int(self.energy / 1000 * 63) < command[1]
        elif cmd == 20:#какой мой возраст
            return int(self.age / 1000 * 63) == command[1]
        elif cmd == 21:#возраст больше аргумента
            return int(self.age / 1000 * 63) > command[1]
        elif cmd == 22:#возраст меньше аргумента
            return int(self.age / 1000 * 63) < command[1]
