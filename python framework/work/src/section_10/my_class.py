class Thing:
    """A simple empty class named Thing."""

class Thing2:
    """A class with a class attribute 'letters'."""
    letters = "abc"


class Thing3:
    """A class with an instance attribute 'letters'."""
    def __init__(self) -> None:
        """Initialize the Thing3 instance with letters set to 'xyz'."""
        self.letters = "xyz"

class Element:
    """A class representing a chemical element."""
    def __init__(self, name: str, symbol: str, number: int) -> None:
        """Initialize the Element instance.

        Args:
            name (str): The name of the element.
            symbol (str): The symbol of the element.
            number (int): The atomic number of the element.
        """
        self.name = name
        self.symbol = symbol
        self.number = number

    def dump(self) -> None:
        """Print the attributes of the element."""
        print(f"{self.name=},{self.symbol=},{self.number=}")


class Element2:
    """A class representing a chemical element with a string representation."""
    def __init__(self, name: str, symbol: str, number: int) -> None:
        """Initialize the Element2 instance.

        Args:
            name (str): The name of the element.
            symbol (str): The symbol of the element.
            number (int): The atomic number of the element.
        """
        self.name = name
        self.symbol = symbol
        self.number = number

    def __str__(self) -> str:
        """Return a string representation of the element.

        Returns:
            str: The string representation of the element.
        """
        return f"{self.name=},{self.symbol=},{self.number=}"


class Element3:
    """A class representing a chemical element with private attributes and properties."""
    def __init__(self, name: str, symbol: str, number: int) -> None:
        """Initialize the Element3 instance.

        Args:
            name (str): The name of the element.
            symbol (str): The symbol of the element.
            number (int): The atomic number of the element.
        """
        self.__name = name
        self.__symbol = symbol
        self.__number = number

    @property
    def name(self) -> str:
        """Get the name of the element.

        Returns:
            str: The name of the element.
        """
        return self.__name

    @property
    def symbol(self) -> str:
        """Get the symbol of the element.

        Returns:
            str: The symbol of the element.
        """
        return self.__symbol

    @property
    def number(self) -> int:
        """Get the atomic number of the element.

        Returns:
            int: The atomic number of the element.
        """
        return self.__number


class Bear:
    """A class representing a Bear."""
    def eats(self) -> str:
        """Return the food that a bear eats.

        Returns:
            str: The food that a bear eats.
        """
        return "berries"


class Rabbit:
    """A class representing a Rabbit."""
    def eats(self) -> str:
        """Return the food that a rabbit eats.

        Returns:
            str: The food that a rabbit eats.
        """
        return "clover"


class Octothorpe:
    """A class representing an Octothorpe."""
    def eats(self) -> str:
        """Return the food that an octothorpe eats.

        Returns:
            str: The food that an octothorpe eats.
        """
        return "campers"


class Laser:
    """A class representing a Laser."""
    def does(self) -> str:
        """Return the action that a laser performs.

        Returns:
            str: The action that a laser performs.
        """
        return "disintegrate"


class Claw:
    """A class representing a Claw."""
    def does(self) -> str:
        """Return the action that a claw performs.

        Returns:
            str: The action that a claw performs.
        """
        return "crush"


class SmartPhone:
    """A class representing a SmartPhone."""
    def does(self) -> str:
        """Return the action that a smartphone performs.

        Returns:
            str: The action that a smartphone performs.
        """
        return "ring"


class Robot:
    """A class representing a Robot."""
    def __init__(self) -> None:
        """Initialize the Robot instance with Laser, Claw, and SmartPhone components."""
        self.laser = Laser()  # Instantiate Laser
        self.claw = Claw()  # Instantiate Claw
        self.smartphone = SmartPhone()  # Instantiate SmartPhone

    def does(self) -> str:
        """Return the actions that the robot can perform.

        Returns:
            str: The actions that the robot can perform.
        """
        return f"I can {self.laser.does()},\nand I can {self.claw.does()},\nand I can {self.smartphone.does()}"
