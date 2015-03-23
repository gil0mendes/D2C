# D2C
Divide-To-Conquer (JAVA RMI Task Dispatching)

# Algumas considerações
* Mais que um Master
* Redundância no TaskBag
* Failover nos works
* Concorrência
* Tratar de um slow-worker
* Recovery entre o Master e o TaskBag

# Caminho (start-to-end)
* (Master) Cria uma tarefa
* (Master) Divide em sub-tarefas
* (Master) Envia as sub-tarefas para o TaskBag
* (TaskBag) Encontra os melhores Works para trabalho
* (TaskBag) Envia a sub-tarefa para o Worker
* (Worker) Executa a sub-tarefa atribuida
* (Worker) Retorna a sub-tarefa ao TaskBag
* (TaskBag) Reencaminha a resposta da sub-tarefa para o Master
* (Master) Espera por todas as sub-tarefas
* (Master) Ordena os sub-resultados e juntaos
* (Master) Apresenta o resultado
