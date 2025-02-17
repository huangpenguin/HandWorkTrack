import sys
from pathlib import Path

current_dir = Path(__file__).parent
src_dir = current_dir / ".." / "src"
sys.path.append(str(src_dir))

from section_9 import OopsExceptionError, get_odds, good, test


def task_9_1() -> None:
    """Print a list of names obtained from the good function."""
    a_list = good()
    print(a_list)
    print("task_9_1 done\n")

def task_9_2() -> None:
    """Print the third odd number from the get_odds generator."""
    count = 1
    for number in get_odds():
        if count == 3:
            print("The third odd number is", number)
            break
        count += 1
    print("task_9_2 done\n")

def task_9_3() -> None:
    """Use the test decorator to wrap a greeting function."""
    @test
    def greeting():
        print("greetings")
    greeting()
    print("task_9_3 done\n")

def task_9_4() -> None:
    """Raise and catch an OopsExceptionError."""
    word = "Oops"
    try:
        if word == "Oops":
            raise OopsExceptionError(word)
    except OopsExceptionError as e:
        print(e)
    print("task_9_4 done\n")

def task_9_5() -> None:
    """Generate and print strings using a generator expression."""
    gen = (f"Got {i}" for i in range(10))
    for thing in gen:
        print(thing)
    print("task_9_5 done\n")

if __name__ == "__main__":
    task_9_1()
    task_9_2()
    task_9_3()
    task_9_4()
    task_9_5()
