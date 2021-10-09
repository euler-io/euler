FROM ubuntu:21.04

ARG OPENCV_VERSION=4.5.1

# Install dependencies
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
    build-essential \
    cmake \
    g++ \
    wget \
    unzip \
    openjdk-11-jre-headless

# Download OpenCV with contrib
RUN mkdir -p /opencv/build && \
    wget -O /tmp/opencv.zip https://github.com/opencv/opencv/archive/${OPENCV_VERSION}.zip && \
    wget -O /tmp/opencv_contrib.zip https://github.com/opencv/opencv_contrib/archive/${OPENCV_VERSION}.zip && \
    unzip /tmp/opencv.zip -d /opencv && \
    unzip /tmp/opencv_contrib.zip -d /opencv
    
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
    qtbase5-dev \
    libqt5gui5

# Compile OpenCV with contrib
RUN cd /opencv/build && \
    cmake -D CMAKE_BUILD_TYPE=RELEASE \
    -D OPENCV_EXTRA_MODULES_PATH=../opencv_contrib-${OPENCV_VERSION}/modules \
    -D CMAKE_INSTALL_PREFIX=/usr/local \
    -D WITH_TBB=ON \
    -D BUILD_NEW_PYTHON_SUPPORT=ON \
    -D WITH_V4L=ON \
    -D INSTALL_C_EXAMPLES=OFF \
    -D INSTALL_PYTHON_EXAMPLES=OFF \
    -D BUILD_EXAMPLES=OFF \
    -D WITH_QT=OFF \
    -D WITH_GTK=OFF \
    -D WITH_OPENGL=ON \
    -DWITH_FFMPEG=ON ../opencv-${OPENCV_VERSION}/ && \
    cmake --build . && \
    make install -j4