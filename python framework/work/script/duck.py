class Duck:
    def walk(self):
        print("walkwalkwalk")
    def swim(self):
        print("swimswimswim")
    def talk(self):
        print("im a duck")

class Penguin:
    def walk(self):
        print("walkwalkwalk")
    def swim(self):
        print("swimswimswim")
    def talk(self):
        print("im a duck")

def duck_show(obj):
    obj.walk()
    obj.swim()
    obj.talk()

duck=Duck()
penguin=Penguin()
duck_show(duck)
print("\n")
duck_show(penguin)
