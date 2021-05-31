//
// Created by admin on 2021/5/30.
//

#include <fcntl.h>
#include "mmap_util.h"
#include <sys/mman.h>
#include <sys/stat.h>
#include <zconf.h>
#include <stdexcept>

// 函数: open, fstat, mmap, memcpy, write
// static_cast, std::string 申请空间
// 乱码
/**
 * 1. open 文件
 * 2. 获取文件大小
 * 3. mmap 映射文件到内存中同样大小的位置
 * 4. memcpy 复制 mmap 映射内存的数据到指定的内存地址并返回
 */
std::string mmap_util::read_string(const std::string &path) {
    auto fd = open(path.c_str(), O_RDWR, S_IRWXU);
    struct stat st = {0};
    size_t size = 0;
    if (fstat(fd, &st) != -1) {
        size = static_cast<size_t>(st.st_size);
    }
    size = ((size / getpagesize()) + 1) * getpagesize();
    void *ptr = mmap(nullptr, size, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    char content[size];
    if (ptr == MAP_FAILED) throw std::runtime_error("MAP_FILED");
    memcpy(&content, ptr, size);
    return content;
}

/**
 * 1. open 文件, 文件需要有个预定的大小
 * 2. 根据预定的大小把文件通过 mmap 映射到内存中的一块地址
 * 3. 通过 memcpy 复制要写入的数据到 mmap 映射的内存区域
 * 4. mmap 映射的文件大小必须是 pagesize 的整数倍
 */
void mmap_util::write_string(std::string path, std::string content) {
    auto fd = open(path.c_str(), O_RDWR | O_CREAT, S_IRWXU);
    struct stat st = {0};
    size_t size = 0;
    if (fstat(fd, &st) != -1) {
        size = static_cast<size_t>(st.st_size);
    }
    size = ((size / getpagesize()) + 1) * getpagesize();

    size_t needFill = size;
    const char zeros[1024] = {0};
    while (needFill > 0) {
        size_t fillSize = 0;
        if (needFill > sizeof(zeros)) {
            fillSize = sizeof(zeros);
        } else {
            fillSize = needFill;
        }
        if (write(fd, zeros, fillSize) < 0) {
            throw std::runtime_error("FILL_FILED");
        }
        needFill -= fillSize;
    }

    void *ptr = mmap(nullptr, size, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if (ptr == MAP_FAILED) throw std::runtime_error("MAP_FILED");
    memcpy(ptr, &content, content.length() + 1);
}