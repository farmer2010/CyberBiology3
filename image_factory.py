import pygame
pygame.init()

def get_image(color):
    img = pygame.Surface((10, 10))
    img.fill((0, 0, 0))
    pygame.draw.rect(img, color, (1, 1, 8, 8))
    return(img)

def get_organics_image():
    img = pygame.Surface((10, 10))
    img.fill((0, 0, 255))
    pygame.draw.rect(img, (0, 0, 0), (1, 1, 8, 8))
    pygame.draw.rect(img, (128, 128, 128), (2, 2, 6, 6))
    img.set_colorkey((0, 0, 255))
    return(img)
