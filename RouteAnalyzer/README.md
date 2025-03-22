# RouteAnalyzer

## Overview

This project analyzes routes based on waypoints and configuration parameters. It uses Docker to containerize the application for easy deployment and execution.

## Prerequisites

- Docker installed on your system
- `custom-parameters.yml` and `waypoints.csv` files in the root directory of the project

## Building the Docker Image

To build the Docker image, run the following command in the root directory of the project:

```sh
sudo docker build -t route-analyzer .
```

## Running the Docker Container
To run the Docker container, use the following command:

```sh
sudo docker run \
    -v $(pwd)/custom-parameters.yml:/app/custom-parameters.yml \
    -v $(pwd)/waypoints.csv:/app/waypoints.csv \
    route-analyzer
```

### Explanation of the Command

- `sudo docker run`: Runs a Docker container.
- `-v $(pwd)/custom-parameters.yml:/app/config/custom-parameters.yml`: Mounts the `custom-parameters.yml` file from the current directory to the container's `/app/config/custom-parameters.yml` path.
- `-v $(pwd)/waypoints.csv:/app/data/waypoints.csv`: Mounts the `waypoints.csv` file from the current directory to the container's `/app/data/waypoints.csv` path.
- `route-analyzer`: The name of the Docker image to run.

## Copying the Output File

To copy the output file from the Docker container to the host machine, use the following command:

```sh
sudo docker cp <container_id>:/app/output.json .
```

To get the container ID, use the following command:

```sh
sudo docker ps -a --filter ancestor=route-analyzer
```

## Stopping the Docker Container

To stop the running Docker container, use the following command:

```sh
sudo docker stop <contener_id>
```

To get the container ID, use the following command:

```sh
sudo docker ps -a --filter ancestor=route-analyzer
```

## Deleting the Docker Container

To delete the stopped Docker container, use the following command:

```sh
sudo docker rm <contener_id>
```

To get the container ID, use the following command:

```sh
sudo docker ps -a --filter ancestor=route-analyzer
```

## Removing the Docker Image

To remove the Docker image, use the following command:

```sh
sudo docker rmi route-analyzer
```
## Additional Information

For more information on Docker commands and usage, refer to the [Docker documentation](https://docs.docker.com/).