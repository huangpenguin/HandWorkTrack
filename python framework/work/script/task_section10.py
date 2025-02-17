import sys
from pathlib import Path

current_dir = Path(__file__).parent
src_dir = current_dir / ".." / "src"
sys.path.append(str(src_dir))

from section_10 import Bear, Element, Element2, Element3, Octothorpe, Rabbit, Robot, Thing, Thing2, Thing3


def task_10_1() -> None:
    """Print the Thing class and create an instance of Thing."""
    print(Thing)
    example = Thing()
    print(example)
    print("do task_10_1")

def task_10_2() -> None:
    """Print the class attribute 'letters' of Thing2."""
    print(Thing2.letters)
    print("do task_10_2")

def task_10_3() -> None:
    """Create an instance of Thing3 and print its 'letters' attribute."""
    sth = Thing3()
    print(sth.letters)
    print("do task_10_3")

def task_10_4() -> None:
    """Create an instance of Element with 'Hydrogen', 'H', 1 as arguments."""
    sth = Element("Hydrogen", "H", 1)
    print("do task_10_4")

def task_10_5() -> None:
    """Create an instance of Element using a dictionary and print its 'name' attribute."""
    el_dict = {"name": "Hydrogen", "symbol": "H", "number": 1}
    hydrogen = Element(**el_dict)
    print(hydrogen.name)
    print("do task_10_5")

def task_10_6() -> None:
    """Create an instance of Element using a dictionary and call its dump method."""
    el_dict = {"name": "Hydrogen", "symbol": "H", "number": 1}
    hydrogen = Element(**el_dict)
    hydrogen.dump()
    print("do task_10_6")

def task_10_7() -> None:
    """Create an instance of Element2 using a dictionary and print it."""
    el_dict = {"name": "Hydrogen", "symbol": "H", "number": 1}
    hydrogen = Element2(**el_dict)
    print(hydrogen)
    print("do task_10_7")

def task_10_8() -> None:
    """Create an instance of Element3 using a dictionary and print its attributes."""
    el_dict = {"name": "Hydrogen", "symbol": "H", "number": 1}
    hydrogen = Element3(**el_dict)
    print(hydrogen.name)
    print(hydrogen.symbol)
    print(hydrogen.number)
    print("do task_10_8")

def task_10_9() -> None:
    """Create instances of Bear, Rabbit, and Octothorpe, and print what they eat."""
    b = Bear()
    r = Rabbit()
    o = Octothorpe()
    print(b.eats())
    print(r.eats())
    print(o.eats())
    print("do task_10_9")

def task_10_10() -> None:
    """Create an instance of Robot and print what it does."""
    robot = Robot()
    print(robot.does())
    print("do task_10_10")

if __name__ == "__main__":
    task_10_1()
    task_10_2()
    task_10_3()
    task_10_4()
    task_10_5()
    task_10_6()
    task_10_7()
    task_10_8()
    task_10_9()
    task_10_10()
