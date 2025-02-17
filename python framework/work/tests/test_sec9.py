import pytest
import sys
from pathlib import Path

current_dir = Path(__file__).parent
src_dir = current_dir / ".." / "src"
sys.path.append(str(src_dir))
from section_9 import get_odds,OopsExceptionError

#test of function
def test_get_odds():
    expected_output = [1, 3, 5, 7, 9]
    assert list(get_odds()) == expected_output

#test of exception class
def test_oops_exception_error():
    with pytest.raises(OopsExceptionError) as exc_info:
        raise OopsExceptionError("Test error message")

    assert isinstance(exc_info.value, OopsExceptionError)

    assert str(exc_info.value) == "MyException: Test error message"
