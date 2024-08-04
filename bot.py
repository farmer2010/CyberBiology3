import copy
import organics
from random import randint as rand
import image_factory
import pygame
from game_object import GameObject
pygame.init()

class Bot(GameObject):
    def __init__(self, pos, color, world, objects, bots, energy=1000, draw_type=0):
        GameObject.__init__(self, pos, image_factory.get_image(color))
        self.name = "bot"#имя
        self.killed = 0#мертв бот или нет
        self.color = color#цвет
        self.rotate = rand(0, 7)#направление
        self.energy = energy#енергия
        self.age = 1000#возраст (больше = бот молодой)
        self.world = world#ссылка на массив с миром
        self.objects = objects#ссылка на массив с ботами
        self.commands = [[rand(0, 8) for y in range(6)]for x in range(5)]#мозг бота
        self.minerals = 0
        self.attack_count = 0#красный в режиме отбражения типа питания
        self.photo_count = 0#зеленый в режиме отбражения типа питания
        self.minerals_count = 0#синий в режиме отбражения типа питания
        self.bots = bots#оличество ботов(для отображения на экране)
        self.attacked = 0#бот был атакован
        self.photo_list = [#массивы с приходом фотосинтеза и минералов в зависимости от уровня
            10,
            8,
            6,
            4,
            3,
            1
            ]
        self.minerals_list = [
            1,
            2,
            3
            ]
        self.last_draw_type = [0]
        self.change_image(draw_type)

    def bot_in_sector(self):#для фотосинтеза и минералов
        sector_len = int(self.world_scale[1] / 8)
        error = self.world_scale[1] - sector_len * 8
        sec = int(self.pos[1] / sector_len)
        if sec > 7:
            return(10)#море(будет, если высота мира нацело не делится на 8)
        return(sec)

    def change_image(self, draw_type):#сменить цвет
        if draw_type == 0:#режим отображения цвета ботов
            self.image = image_factory.get_image(self.color)
        elif draw_type == 1:#режим отображения энергии
            g = 255 - int((self.energy / 1000) * 255)
            if g < 0:
                g = 0
            try:
                self.image = image_factory.get_image((255, g, 0))
            except:
                print(g)
        elif draw_type == 2:#ежим отображения минералов
            rg = 255 - int((self.minerals / 1000) * 255)
            if rg < 0:
                rg = 0
            self.image = image_factory.get_image(
                (
                    rg,
                    rg,
                    255
                    )
                )
        elif draw_type == 3:#режим отображения возраста
            self.image = image_factory.get_image(
                (
                    int((self.age / 1000) * 255),
                    int((self.age / 1000) * 255),
                    int((self.age / 1000) * 255)
                    )
                )
        elif draw_type == 4:#режим отображения типа питания(хищников)
            count = sum((self.photo_count, self.attack_count, self.minerals_count))
            if count == 0:
                R = 128
                G = 128
                B = 128
            else:
                R = int(self.attack_count / count * 255)
                G = int(self.photo_count / count * 255)
                B = int(self.minerals_count / count * 255)
            self.image = image_factory.get_image((R, G, B))

    def multiply(self, draw_type, rotate):#поделиться
        pos2 = self.get_rotate_position(rotate)#позиция, на которую смотрит бот
        if pos2[1] >= 0 and pos2[1] <= self.world_scale[1] - 1:#если бот не смотрит в стену
            if self.world[pos2[0]][pos2[1]] == "none":#если перед ботом ничего нет
                self.energy -= 150#деление требует 150 ед. энергии
                if self.energy <= 0:#если энергии не хватает, то умереть
                    self.killed = 1
                    self.world[self.pos[0]][self.pos[1]] = "none"
                    self.kill()
                else:#если энергии хватает
                    new_bot = Bot(pos2, self.color, self.world, self.objects, self.bots, energy=int(self.energy * 0.5), draw_type=draw_type)#создать нового бота
                    #настройка данных, изменяющихся при мутации
                    new_commands = copy.deepcopy(self.commands)
                    new_color = self.color
                    if rand(0, 3) == 0:#мутация с шансом 1/4
                        #мутация потомка
                        new_commands[rand(0, 4)][rand(0, 5)] = rand(0, 8)#мутация мозга
                        new_color = (
                            rand(0, 255),
                            rand(0, 255),
                            rand(0, 255)
                        )
                    new_bot.color = new_color#задать потомку цвет
                    new_bot.commands = new_commands#дать потомку мозг
                    #минералы и энергия распределяются равномерно между потомком и предком
                    new_bot.minerals = int(self.minerals / 2)
                    self.minerals = int(self.minerals / 2)
                    self.energy = int(self.energy / 2)
                    self.objects.add(new_bot)#добавить в массив с ботами потомка
                    self.world[pos2[0]][pos2[1]] = "bot"#записать в массив с миром, в какой клетке стоит бот

    def get_rotate_position(self, rotate):#вычисление координат на которые смотрит бот
        pos = [
            (self.pos[0] + GameObject.movelist[rotate][0]) % self.world_scale[0],#мир зациклен по горизонтали
            self.pos[1] + GameObject.movelist[rotate][1]
            ]
        return(pos)

    def attack(self, pos):#атаковать
        if pos[1] >= 0 and pos[1] <= self.border - 1:#границы
            if self.world[pos[0]][pos[1]] == "bot" or self.world[pos[0]][pos[1]] == "organics":#если есть цель
                victim = None
                for victim in self.objects:#поиск жертвы
                    if victim.pos == pos:
                        break
                    else:
                        victim = None
                if victim != None:#если есть жертва
                    if victim.energy > 150:#отнять у жертвы 150 энергии
                        self.energy += 150
                        victim.energy -= 150
                        victim.attacked = 1
                    else:#убить, если энергии меньше 0(можно было и не убивать)
                        self.energy += victim.energy
                        victim.killed = 1
                        victim.kill()
                        self.world[pos[0]][pos[1]] = "none"
                    self.attack_count += 1#бот краснеет

    def give(self, pos):#отдать соседу 1/4 своих ресурсов
        friend = None#поиск соседа
        for friend in self.objects:
            if friend.pos == pos and friend.name == "bot":
                break
            else:
                friend = None
        if friend != None:#если есть сосед, то отдать ему часть своих ресурсов
            friend.energy += int(self.energy / 4)
            friend.minerals += int(self.minerals / 4)
            self.energy -= int(self.energy / 4)
            self.minerals -= int(self.minerals / 4)

    def update_commands(self, draw_type):
        x = self.sensor(self.world, self.rotate) - 1#столбец в мозге(то что перед ботом)
        check = [#список раздражителей
            self.attacked,#бот атакован
            self.energy < 100,#мало энергии
            self.age < 100,#бот старый
            self.energy > 900,#много энергии
            self.age > 900,#бот молодой
            1#срабатывает, если все остальные не сработали
            ]
        y = 0#строка в мозге(первый сработавший раздражитель)
        for y in range(len(check)):#поиск первого сработавшего раздражителя
            if check[y] == 1:
                break
        command = self.commands[x][y]#команда на перекрестии сработавших столбца и строки
        if command == 0:#ничего не делать(спорная команда, думаю удалить в последующих версиях)
            pass
        elif command == 1:#повернуть налево
            self.rotate -= 1
            self.rotate %= 8
        elif command == 2:#повернуть направо
            self.rotate += 1
            self.rotate %= 8
        elif command == 3:#походить
            self.move(self.world)
        elif command == 4:#атаковать
            self.attack(self.get_rotate_position(self.rotate))
        elif command == 5:#поделиться
            self.multiply(draw_type, self.rotate)
        elif command == 6:#заниматься фотосинтезом
            sector = self.bot_in_sector()
            if sector <= 5:
                self.photo_count += 1
                self.energy += self.photo_list[sector]
        elif command == 7:#преобразовать минералы в энергию
            if self.minerals > 0:
                self.minerals_count += 1
                self.energy += self.minerals * 4
                self.minerals = 0
        elif command == 8:#отдать соседу часть своих ресурсов
            self.give(self.get_rotate_position(self.rotate))
        elif command > 8:#неиспользуемые команды(поворачивают бота в определенное направление)
            self.rotate = command - 9

    def update(self, draw_type):
        self.bots[0] += 1#величить счетчик ботов на один
        if not self.killed:#если бот не мертв:
            self.world[self.pos[0]][self.pos[1]] = "bot"
            self.age -= 1#постареть
            self.energy -= 1#уменьшить количество энергии
            sector = self.bot_in_sector()#для минералов
            if sector <= 7 and sector >= 5:#приход минералов
                self.minerals += self.minerals_list[sector - 5]
            if draw_type != self.last_draw_type:#сменить режим отрисовки
                self.last_draw_type[0] = draw_type
                self.change_image(draw_type)
            self.update_commands(draw_type)#обновить мозг
            if self.energy > 1000:#ограничитель количества энергии
                self.energy = 1000
            if self.minerals > 1000:#ограничитель количества минералов
                self.minerals = 1000
            if self.age <= 0:#умереть от старости(органика появляется)
                self.world[self.pos[0]][self.pos[1]] = "organics"
                self.objects.add(organics.Organics(self.pos, self.world, self.objects, self.energy))
                self.killed = 1
                self.kill()
            if self.energy <= 0:#мереть от недостатка энергии(органика не появляется)
                self.world[self.pos[0]][self.pos[1]] = "none"
                self.killed = 1
                self.kill()
            if self.attacked == 1:#если бот атакован, то он не атакован(    :) Извините, не знаю, как это по-другому описать    )
                self.attacked = 0
