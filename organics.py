from game_object import GameObject
import image_factory
import pygame
pygame.init()

class Organics(GameObject):
    def __init__(self, pos, world, objects, energy=0):
        GameObject.__init__(self, pos, image_factory.get_organics_image())
        self.name = "organics"#
        self.rotate = 4#направление(можно изменить, тогда органика будет лететь в другое место)
        self.energy = energy#энергия
        self.world = world#ссылка на массив с миром
        self.objects = objects#ссылка на массив с ботами
        self.killed = 0#убита или нет
        self.is_falling = 1#падает или нет

    def update(self, a):#a - это тип отрисовки(он органике не нужен)
        if not self.killed:#если органика не мертва(как бы странно это не звучало)
            self.world[self.pos[0]][self.pos[1]] = "organics"
            if self.is_falling:#если падает, то падать(вот это поворот!)
                self.move(self.world)
                sensor = self.sensor(self.world, self.rotate)
                if sensor != 2:#если под органикой препятствие, то она перестает падать
                    self.is_falling = 0
