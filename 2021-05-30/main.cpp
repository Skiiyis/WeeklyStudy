#include "mmap_util.h"
#include <string>
#include <iostream>

int main() {
    std::string content = mmap_util::read_string("/Users/admin/Documents/WeeklyStudy/2021-05-30/CMakeLists.txt");
    std::cout << content << std::endl;

    /*mmap_util::write_string("/Users/admin/Documents/WeeklyStudy/2021-05-30/Test.txt",
                            "I'm niubility");*/
}