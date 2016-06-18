#include <stdio.h>
#include <stdlib.h>
#include <strings.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>
#include <netinet/in.h>
#include <signal.h>

#define PORT 8001
#define IP "192.168.199.116"

//extern void sig_proccess(int signo);
//extern void sig_pipe(int signo);

char* socket_core(char*);
char* process_client(int, char*);

int main(int argc, char* argv[])
{
	char* result = socket_core("CREATE TABLE test(testId INTEGER PRIMARY KEY, testString TEXT)");
	printf("Receive = %s\n", result);
	return 0;
}

char* socket_core(char* buffer)
{
	int client_sockfd;
	struct sockaddr_in server_addr;
	int err;

	//signal(SIGINT, sig_proccess);
	//signal(SIGPIPE, sig_pipe);

	client_sockfd = socket(PF_INET, SOCK_STREAM, 0);
	if (client_sockfd < 0)
	{
		printf("Socket Client's socket error\n");
		return "socket error";
	}


	bzero(&server_addr, sizeof(server_addr));
	server_addr.sin_family = AF_INET;
	server_addr.sin_addr.s_addr = inet_addr(IP);
	server_addr.sin_port = htons(PORT);

	err = connect(client_sockfd, (struct sockaddr*)&server_addr, sizeof(struct sockaddr));
	if (err < 0 )
	{
		printf("Socket Client's connect error\n");
		return "connect error";
	}
	char* result = process_client(client_sockfd, buffer);
	close(client_sockfd);
	return result;
}

char* process_client(int client_sockfd,char* buffer)
{
	int size = 0;
	//char buffer[1024];

	//while(1)
	//{
		printf("Enter String to send:\n");
	//	scanf("%s", buffer);
		size = send(client_sockfd, buffer, strlen(buffer), 0);
		size = recv(client_sockfd, buffer, 1024, 0);
		//buffer[size] = '/0';
		//printf("received:%s\n", buffer);
	//}
	return buffer;
}
