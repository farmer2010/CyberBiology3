#модули
import buttons
import pygame_widgets
from random import randint as rand
import bot
import pygame
from PIL import Image
import copy
pygame.init()

#НАСТРОЙКА

keep_going = True
pause = [False]
render_brain = [False]
mouse = ["select"]
botcode = [None]
bots = [0]
white = (255, 255, 255)
grey = (90, 90, 90)

W = pygame.display.Info().current_w
H = pygame.display.Info().current_h
#W = 900#для отладки
#H = 900
screen = pygame.display.set_mode([W, H])
description = "Cyber Biology 3 v2.2"
pygame.display.set_caption(description)
objects = pygame.sprite.Group()
world_scale = [
    int((W - 300) / 10),
    int(H / 10)
    ]
world_scale = [60, 60]
world = [["none" for y in range(world_scale[1])]for x in range(world_scale[0])]
draw_type = [0]
font = pygame.font.SysFont(None, 24)
buttons.init([draw_type, pause, render_brain, mouse, botcode])
timer = pygame.time.Clock()
selection = None
input_name = None
save_button = None
with Image.open("organics.png") as organics_img:
    organics_img.load()
with Image.open("window.png") as win_img:
    win_img.load()
black = Image.new('RGB', (10, 10), (0, 0, 0))

#ОСНОВНОЕ

def render_text(text, pos, color=(0, 0, 0), size=24, centerx=False, centery=False):#отрисовка текста на экране
    font = pygame.font.SysFont(None, size)
    text_img = font.render(text, True, color)
    text_rect = text_img.get_rect()
    if centerx:
        text_rect.centerx = pos[0]
    else:
        text_rect.x = pos[0]
    if centery:
        text_rect.centery = pos[1]
    else:
        text_rect.y = pos[1]
    screen.blit(text_img, text_rect)

def draw_brain(brain):#отрисовка мозга
    for x in range(len(brain)):
        for y in range(len(brain[x])):
            pos = [
                x * 45 + 605,
                y * 45 + 5
                ]
            pygame.draw.rect(screen, (128, 128, 128), (pos[0], pos[1], 40, 40))
            render_text(str(brain[x][y]), (pos[0] + 20, pos[1] + 20), centerx=True, centery=True)

def mouse_function():#обработка нажатий мыши
    global selection
    mousepos = pygame.mouse.get_pos()
    botpos = [#позиция объекта, на котонрый нажали
        int(mousepos[0] / 10),
        int(mousepos[1] / 10)
        ]
    if mouse[0] == "select":#выбрать бота
        if botpos[0] < world_scale[0]:
            for u in objects:
                if u.name == "bot":
                    if u.pos == botpos:
                        if selection != u:
                            buttons.set_bot_buttons(u)
                            selection = u
                            break
                    else:
                        buttons.remove_bot_buttons()
                        selection = None
                else:
                    buttons.remove_bot_buttons()
                    selection = None
    elif mouse[0] == "remove":#удалить объект(и бота, и органику)
        if botpos[0] < world_scale[0]:
            for u in objects:
                if u.pos == botpos:
                    u.kill()
                    break
            world[botpos[0]][botpos[1]] = "none"
    elif mouse[0] == "set":#установить загруженного бота
        if botpos[0] < world_scale[0]:
            if botcode[0] != None:
                if world[botpos[0]][botpos[1]] == "none":
                    new_bot = bot.Bot(botpos.copy(), (0, 0, 255), world, objects, bots)
                    new_bot.commands = botcode[0].copy()
                    objects.add(new_bot)
                    world[botpos[0]][botpos[1]] = "bot"

steps = 0
steps2 = 0
mousedown = 0
render = 1
record = 0

while keep_going:#основной цикл
    steps2 += 1
    steps2 %= 60
    events = pygame.event.get()
    for event in events:
        if event.type == pygame.QUIT:
            keep_going = False
        if event.type == pygame.MOUSEBUTTONDOWN:#нажатия мыши
            mousedown = 1
        if event.type == pygame.MOUSEBUTTONUP:
            mousedown = 0
        if event.type == pygame.KEYDOWN:#обработка нажатий клавиш
            if event.key == pygame.K_ESCAPE:
                keep_going = False
            if event.key == pygame.K_F1:
                steps = 0#создать новую популяцию
                for i in objects:
                    i.kill()
                    world[i.pos[0]][i.pos[1]] = "none"
                for x in range(1000):#количество создаваемых существ
                    while True:#перебирать ботов, пока на клетке, на которой он должен появиться, не окажется пустота
                        color = (
                            rand(0, 255),
                            rand(0, 255),
                            rand(0, 255)
                            )
                        pos = [
                            rand(0, world_scale[0] - 1),
                            rand(0, world_scale[1] - 1)
                            ]
                        if world[pos[0]][pos[1]] == "none":
                            objects.add(bot.Bot(pos, color, world, objects, bots))
                            world[pos[0]][pos[1]] = "bot"
                            break
            if event.key == pygame.K_F2:#включение/выключение отрисовки
                render = not render
            if event.key == pygame.K_F3:#включение/выключение записи
                record = not record
    if not pause[0]:#обновить всех ботов
        steps += 1
        bots[0] = 0
        objects.update(draw_type[0])
    if render:
        screen.fill(grey)
        pygame.draw.rect(screen, (255, 255, 255), (0, 0, world_scale[0] * 10, world_scale[1] * 10))
        if mousedown:#установить/уничтожить/выделить
            mouse_function()
        objects.draw(screen)#отрисовка ботов
        #for x in range(60):#для отладки
        #    for y in range(60):
        #        if world[x][y] == "bot":
        #            pygame.draw.rect(screen, (0, 255, 0), (x * 10, y * 10, 5, 5))
        #        elif world[x][y] == "organics":
        #            pygame.draw.rect(screen, (255, 255, 0), (x * 10, y * 10, 5, 5))
        pygame_widgets.update(events)#нопки
        #рисование текста
        if draw_type[0] == 0:
            txt = "Bot color view"
        elif draw_type[0] == 1:
            txt = "Bot energy view"
        elif draw_type[0] == 2:
            txt = "Bot minerals view"
        elif draw_type[0] == 3:
            txt = "Bot age view"
        else:
            txt = "Predators view"
        render_text("Main:", (W - 300, 0))
        render_text("Steps: " + str(steps) + ", fps: " + str(int(timer.get_fps())), (W - 300, 20))
        render_text("Objects: " + str(len(objects)) + ", bots: " + str(bots[0]), (W - 300, 40))
        render_text("Render type: " + txt, (W - 300, 60))
        render_text("Render types:", (W - 300, 160))
        render_text("Selection:", (W - 300, 400))
        render_text("Mouse:", (W - 300, 600))
        render_text("Mouse function: " + mouse[0], (W - 300, 80))
        render_text("Load:", (W - 300, 760))
        if selection == None:
            render_brain[0] = False
            render_text("None", (W - 300, 420))
        else:#если выбран бот, вывести на экран информацию о нем
            if render_brain[0]:
                draw_brain(selection.commands)
            if int(steps2 // 30) % 2:#мигающее красное выделение
                pygame.draw.rect(screen, (255, 0, 0), selection.rect)
            render_text("Energy: " + str(selection.energy), (W - 300, 420))
            render_text("Minerals: " + str(selection.minerals), (W - 300, 440))
            render_text("Age: " + str(selection.age), (W - 300, 460))
            render_text("Position: " + str(selection.pos), (W - 300, 480))
            render_text("Genes: ", (W - 300, 500))
            for i in range(len(selection.genes)):
                pygame.draw.rect(screen, (0, 0, 0), (W - 240 + 20 * i, 500, 15, 15))
                if selection.genes[i] == 1:
                    pygame.draw.rect(screen, (0, 255, 0), (W - 240 + 20 * i + 2, 502, 11, 11))
                else:
                    pygame.draw.rect(screen, (255, 0, 0), (W - 240 + 20 * i + 2, 502, 11, 11))
            if world[selection.pos[0]][selection.pos[1]] != "bot":
                buttons.remove_bot_buttons()
                selection = None
        pygame.display.update()
    if steps % 25 == 0 and record:#запись
        win_img2 = copy.deepcopy(win_img)
        win_img3 = copy.deepcopy(win_img)
        win_img4 = copy.deepcopy(win_img)
        for obj in objects:
            if obj.name == "organics":
                win_img2.paste(organics_img, (obj.pos[0] * 10, obj.pos[1] * 10))
                win_img3.paste(organics_img, (obj.pos[0] * 10, obj.pos[1] * 10))
                win_img4.paste(organics_img, (obj.pos[0] * 10, obj.pos[1] * 10))
            else:
                bot_img = Image.new('RGB', (8, 8), (0, 128 * obj.genes[0] + 127 * obj.genes[1], 128 * obj.genes[2] + 127 * obj.genes[3]))
                g = 255 - int((obj.energy / 1000) * 255)
                if g < 0:
                    g = 0
                color = (255, g, 0)
                bot_energy_img = Image.new('RGB', (8, 8), color)
                count = sum((obj.photo_count, obj.attack_count, obj.minerals_count))
                if count == 0:
                    R = 128
                    G = 128
                    B = 128
                else:
                    R = int(obj.attack_count / count * 255)
                    G = int(obj.photo_count / count * 255)
                    B = int(obj.minerals_count / count * 255)
                bot_predators_img = Image.new('RGB', (8, 8), (R, G, B))
                win_img2.paste(black, (obj.pos[0] * 10, obj.pos[1] * 10))
                win_img3.paste(black, (obj.pos[0] * 10, obj.pos[1] * 10))
                win_img4.paste(black, (obj.pos[0] * 10, obj.pos[1] * 10))
                win_img2.paste(bot_img, (obj.pos[0] * 10 + 1, obj.pos[1] * 10 + 1))
                win_img3.paste(bot_energy_img, (obj.pos[0] * 10 + 1, obj.pos[1] * 10 + 1))
                win_img4.paste(bot_predators_img, (obj.pos[0] * 10 + 1, obj.pos[1] * 10 + 1))
        win_img2.save(f"record/color/screen{steps // 25}.png")
        win_img3.save(f"record/energy/screen{steps // 25}.png")
        win_img4.save(f"record/predators/screen{steps // 25}.png")
    timer.tick(240)
pygame.quit()
