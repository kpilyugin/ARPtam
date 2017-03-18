#ifndef ARPTAM_LOGGER_H
#define ARPTAM_LOGGER_H

#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include <unistd.h>

static int pfd[2];
static pthread_t thr;
static const char* tag = "myapp";

static void* thread_func(void*);
int start_logger(const char* app_name);

#endif //ARPTAM_LOGGER_H
