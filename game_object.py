import pygame
pygame.init()

def is_relative(brain1, brain2):#функция проверки родственников(если 29 из 30 команд совпадают)
    errors = 0
    if brain1 == brain2:
        return(True)
    for x in range(5):
        for y in range(6):
            if brain1[x][y] != brain2[x][y]:
                errors += 1
            if errors > 1:
                return(False)
    if errors <= 1:
        return(True)
    else:
        return(False)

class GameObject(pygame.sprite.Sprite):#game_object - класс - потомок бота и органики
    movelist = [#массив с поворотами(для движения)
        (0, -1),
        (1, -1),
        (1, 0),
        (1, 1),
        (0, 1),
        (-1, 1),
        (-1, 0),
        (-1, -1),
        ]
    def __init__(self, pos, image):
        pygame.sprite.Sprite.__init__(self)
        self.name = None#имя(изменяется потомками)
        self.rotate = 0#направление(изменяется потомками)
        self.image = image#картинка
        self.pos = pos#позиция
        self.rect = self.image.get_rect()#настройка местоположения на экране
        self.rect.x = self.pos[0] * 10
        self.rect.y = self.pos[1] * 10
        self.W = pygame.display.Info().current_w#настройка размеров экрана
        self.H = pygame.display.Info().current_h
        self.world_scale = [#настройка размера мира
            int((self.W - 300) / 10),
            int(self.H / 10)
            ]
        self.world_scale = [60, 60]
        self.border = self.world_scale[1]

    def update(self):#обновление(изменяется потомками)
        pass

    def move(self, world):#переместиться
        pos2 = [#позиция, не которую смотрит game_object
            (self.pos[0] + GameObject.movelist[self.rotate][0]) % self.world_scale[0],
            self.pos[1] + GameObject.movelist[self.rotate][1]
            ]
        if pos2[1] >= 0 and pos2[1] <= self.border - 1:#если можно сдвинуться
            if world[pos2[0]][pos2[1]] == "none":
                world[self.pos[0]][self.pos[1]] = "none"#перемещение
                world[pos2[0]][pos2[1]] = self.name
                self.pos[0] += GameObject.movelist[self.rotate][0]
                self.pos[0] %= self.world_scale[0]
                self.pos[1] += GameObject.movelist[self.rotate][1]
                self.rect.x = self.pos[0] * 10
                self.rect.y = self.pos[1] * 10

    def sensor(self, world, rotate):#сенсор
        #1 - граница экрана, 2 - ничего, 3 - враг, 4 - родственник, 5 - органика
        pos2 = [#позиция, не которую смотрит game_object
            (self.pos[0] + GameObject.movelist[rotate][0]) % self.world_scale[0],
            self.pos[1] + GameObject.movelist[rotate][1]
            ]
        if pos2[1] >= 0 and pos2[1] <= self.world_scale[1] - 1:#если бот не смотрит в границу(иначе вернуть 1)
            if world[pos2[0]][pos2[1]] == "bot":#если перед сенсором бот
                for select_bot in self.objects:#поиск существа перед сенсором
                    if select_bot.pos == pos2 and select_bot.killed == 0:
                        break
                    else:
                        select_bot = None
                if select_bot != None:
                    if self.name == "bot":#если мое имя - бот(органика для движения также использует сенсор), иначе вернуть 3
                        try:
                            if is_relative(self.commands, select_bot.commands):#если я и сосед - родственники, то вернуть 4, иначе вернуть 3
                                return 4
                            else:
                                return 3
                        except:
                            #print(1)#для отладки(есть очень редкий баг, при котором в массиве с миром на клетке стоит бот, хотя на самом деле там стоит органика. Минут пять миры перебирал, так и не попался он мне
                            return 5#Встречается иногда при появлении органики, иногда позже. Когда попытался запечатлеть момент проявления, баг перестал появляться.)
                    else:
                        return 3
            elif world[pos2[0]][pos2[1]] == "organics":#если перед сенсором органика, то вернуть 5
                return 5
            elif world[pos2[0]][pos2[1]] == "none":#если перед сенсором органика, то вернуть 2
                return 2
        else:
            return 1
