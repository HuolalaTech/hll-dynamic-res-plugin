cmake_minimum_required(VERSION 3.10.2)
#project("nativelib")

add_library(
        nativelib
        SHARED
        nativelib.cpp
)

find_library(
        log-lib
        log
)


target_link_libraries(
        nativelib
        ${log-lib}
)


add_library(
        dynamiclib
        SHARED
        dynamiclib.cpp
)

target_link_libraries(
        dynamiclib
)