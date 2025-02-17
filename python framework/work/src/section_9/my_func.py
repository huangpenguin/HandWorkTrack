from collections.abc import Generator


def good() -> list[str]:
    """Returns a list of names.

    Returns:
        list[str]: A list containing the names "Harry", "Ron", and "Hermione".
    """
    return ["Harry", "Ron", "Hermione"]

def get_odds() -> Generator[int, None, None]:
    """Generates odd numbers from 1 to 9.

    Yields:
        int: The next odd number in the sequence.
    """
    yield from range(1, 10, 2)

def test(func: callable) -> callable:
    """A decorator that prints 'start' before the function call and 'end' after the function call.

    Args:
        func (callable): The function to be decorated.

    Returns:
        callable: The wrapped function with added print statements.
    """
    def wrapper(*args: any, **kwargs: any) -> any:
        """Wrapper function that prints messages before and after the function call.

        Args:
            *args (any): Positional arguments for the wrapped function.
            **kwargs (any): Keyword arguments for the wrapped function.

        Returns:
            any: The result of the wrapped function call.
        """
        print("start")
        result = func(*args, **kwargs)
        print("end")
        return result
    return wrapper

class OopsExceptionError(Exception):
    """Custom exception class for OopsExceptionError.

    Args:
        word (str): The message to be included in the exception.

    Attributes:
        message (str): The formatted exception message.
    """
    def __init__(self, word: str) -> None:
        super().__init__(f"MyException: {word}")
