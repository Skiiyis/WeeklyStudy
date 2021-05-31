//
// Created by admin on 2021/5/30.
//

#ifndef INC_2021_05_30_MMAP_UTIL_H
#define INC_2021_05_30_MMAP_UTIL_H

#include <string>

class mmap_util {

public:

    static std::string read_string(const std::string &path);

    static void write_string(std::string path, std::string content);

};


#endif //INC_2021_05_30_MMAP_UTIL_H
