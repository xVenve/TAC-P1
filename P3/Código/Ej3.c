//%%writefile grafos2.c
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define MAX_NODOS 2000

unsigned long long cont = 0;
int *matriz[MAX_NODOS];
int clusters[MAX_NODOS];

/**
 * It's a wrapper for malloc() that prints an error message and exits if malloc() returns NULL
 *
 * @param nbytes The number of bytes to allocate.
 *
 * @return A pointer to the first byte of the allocated memory.
 */
char *mi_malloc(int nbytes)
{
    char *p;
    static long int nb = 0L;
    static int nv = 0;

    p = malloc(nbytes);
    if (p == NULL)
    {
        fprintf(stderr, "Error, no queda memoria disponible para %d bytes mas\n", nbytes);
        fprintf(stderr, "Se han reservado %ld bytes en %d llamadas\n", nb, nv);
        exit(0);
    }

    nb += (long)nbytes;
    nv++;

    return p;
}

/**
 * It creates a matrix of MAX_NODOS x MAX_NODOS, where each element is an integer
 */
void crear_matriz()
{
    int i;

    for (i = 0; i < MAX_NODOS; i++)
    {
        matriz[i] = (int *)mi_malloc(sizeof(int) * MAX_NODOS);
    }
}

/**
 * It initializes a matrix to all zeros, not conected
 *
 * @param nodos number of nodes
 */
void inicializar_grafo(int nodos)
{
    int i;
    int j;

    for (i = 0; i < nodos; i++)
    {
        for (j = 0; j < nodos; j++)
        {
            matriz[i][j] = 0;
        }
    }
}

/**
 * It creates a random graph with a given number of nodes and edges
 *
 * @param nodos number of nodes
 * @param arcos number of edges
 */
void crear_grafo(int nodos, int arcos)
{
    int i;
    int p;
    int q;

    inicializar_grafo(nodos);

    for (i = 0; i < arcos; i++)
    {
        do
        {
            p = rand() % nodos;
            q = rand() % nodos;
        } while (p == q || matriz[p][q] != 0); // evitar diagonal y arcos existentes
        matriz[p][q] = 1;
        matriz[q][p] = 1; // arista simétrica
    }
}

/**
 * It picks a random edge, and deletes it
 *
 * @param nodos number of nodes
 * @param arcos number of arcs
 *
 * @return The number of arcs in the graph.
 */
int borrar_arco_aleatorio(int nodos, int arcos)
{
    int p;
    int q;

    if (arcos == 0)
    {
        return 0;
    }

    do
    {
        p = rand() % nodos;
        q = rand() % nodos;
    } while (matriz[p][q] == 0 || p == q); // p==q es redundante

    matriz[p][q] = 0;
    matriz[q][p] = 0;

    return arcos - 1;
}

/**
 * It takes a node and a cluster number, and marks all the nodes connected to the first node with the
 * second number
 *
 * @param nodos number of nodes
 * @param n number of nodes
 * @param n_cluster the cluster number
 */
void propagar_marcaR(int nodos, int n, int n_cluster)
{ // T(n) = 2 + n(3 + 3 + 2 + T(n-1)) = 2 + n*(8 + T(n-1))
    int j;
    cont += 2;
    for (j = 0; j < nodos; j++)
    {
        cont += 3;
        cont += 3;
        if (n != j && matriz[n][j] != 0 && clusters[j] == -1)
        {
            cont += 2;
            clusters[j] = n_cluster;
            propagar_marcaR(nodos, j, n_cluster);
        }
    }
}

/* wrapper para propagar una marca (número de cluster) a los nodos adyacentes  */
int contar_clusters(int nodos)
{ // T(n)= 2 + n(4) + 3 + n(6 + LLAMADA + 2)
    int i;
    int n_cluster;
    cont += 2;
    for (i = 0; i < nodos; i++)
    {
        cont += 4;
        clusters[i] = -1; // todos los nodos sin marcar para empezar
    }
    n_cluster = 0;
    cont += 3;
    for (i = 0; i < nodos; i++)
    {
        cont += 4;
        if (clusters[i] == -1)
        { // nodo i no está marcado ==> es nodo de grafo inconexo
            cont += 4;
            clusters[i] = n_cluster; // nuevo cluster y propagar a sus adyacentes
            propagar_marcaR(nodos, i, n_cluster);
            n_cluster++;
        }
    }

    return n_cluster;
}

/**
 * It creates a complete graph, then removes edges at random until there are no edges left
 *
 * @param nodos number of nodes
 */
void analizar_grafo(int nodos)
{
    int arcos;
    int cluster0;
    int cluster1;

    clock_t start, startT;
    clock_t end, endT;
    double cpu_time_used, cpu_time_usedT;

    arcos = nodos * (nodos - 1) / 2;

    crear_grafo(nodos, arcos); // crea grafo completo

    start = clock();
    cluster0 = 0;

    do
    {
        startT = clock();
        cluster1 = contar_clusters(nodos);
        endT = clock();
        cpu_time_usedT = ((double)(endT - startT)) / CLOCKS_PER_SEC;
        printf("Arcos\t %4d\t Tiempo\t %3f \n", arcos, cpu_time_usedT);
        if (cluster1 > cluster0)
        { // imprime solo si aparece nuevo cluster
            cpu_time_usedT = ((double)(endT - startT)) / CLOCKS_PER_SEC;
            printf("Nodos\t %4d\t Arcos\t %4d\t Clusters\t %3d\t Tiempo \t %3f\n", nodos, arcos, cluster1,
                   cpu_time_usedT);
            cluster0 = cluster1;
        } // elimina un arco al azar:
        arcos = borrar_arco_aleatorio(nodos, arcos);
    } while (arcos > 0);

    startT = clock();
    cluster1 = contar_clusters(nodos);
    endT = clock();
    if (cluster1 > cluster0)
    { // imprime solo si aparece nuevo cluster
        cpu_time_usedT = ((double)(endT - startT)) / CLOCKS_PER_SEC;
        printf("Nodos\t %4d\t Arcos\t %4d\t Clusters\t %3d\t Tiempo \t %3f\n", nodos, arcos, cluster1, cpu_time_usedT);
        cluster0 = cluster1;
    }

    end = clock();
    cpu_time_used = ((double)(end - start)) / CLOCKS_PER_SEC;
    printf("Tiempo %3f \n", cpu_time_used);
}

int asignaciones[MAX_NODOS];
int conexiones[MAX_NODOS];
int indices[MAX_NODOS];
unsigned long long sin_conflictos = 0L;
unsigned long long con_conflictos = 0L;

/* imprime nodos del grafo en diagonal y en las intersecciones si son adyacentes */
/* imprime antes el número de grafo inconexo (cluster) si está disponible y el color asignado, si lo hay */

/**
 * It prints the adjacency matrix of the graph
 *
 * @param nodos number of nodes
 */
void imprimir_grafo(int nodos)
{
    int i;
    int j;

    for (i = 0; i < nodos; i++)
    {
        printf("%4d, clust:%3d:, col:%1d ", i, clusters[i], asignaciones[i]);
        for (j = 0; j < nodos; j++)
        {
            if (i == j)
            { // marca diagonal con punto
                printf(".");
            }
            else if (matriz[i][j] == 1)
            {                // si i-j son adyacentes
                printf("+"); // marca intersección i-j con +
            }
            else
                printf(" ");
        }
        printf("\n");
    }
}

/* dada una asignacion de colores en asignaciones comprueba si dos nodos i, j
 estan conectados y en tal caso no coinciden en colores.                        */
int comprueba_conflictos(int nodos)
{
    // T(n)= 1+3+(n-1)*(4+3+ p*(3+1+1+2)); p=(n)/2
    // T(n)= 1/2 (-6 + 7 n + 7 n^2) aprox n^2
    int i;
    int j;
    unsigned long long cf = 0L;
    cont += 4;
    for (i = 0; i < nodos - 1; i++)
    {
        cont += 7;
        for (j = i + 1; j < nodos; j++)
        {
            cont += 4;
            if (matriz[i][j] == 1)
            {
                cont += 1;
                if (asignaciones[i] == asignaciones[j])
                {
                    cont += 2;
                    cf++;
                }
            }
        }
    }
    return cf;
}

/**
 * It checks if there is a conflict between the node and its neighbors
 *
 * @param nodo the node we're checking
 * @param nodos number of nodes
 *
 * @return if there is a conflict or not.
 */
int comprueba_adyacencia(int nodo, int nodos)
{
    // T(n)= 2+ 8n aprox n
    cont += 2;
    for (int i = 0; i < nodos; i++)
    {
        cont += 6;
        if (matriz[nodo][i] == 1 && i != nodo && asignaciones[nodo] == asignaciones[i])
        {
            cont += 2;
            con_conflictos++;
            return 1; // there is a conflict
        }
    }
    return 0; // there is no conflict
}

/**
 * It merges two sorted subarrays into a single sorted subarray.
 *
 * @param p starting index of the sub-array
 * @param q the middle of the array
 * @param r the last index of the array
 */
void merge(int p, int q, int r)
{
    cont += 5;
    // Create L ← A[p..q] and M ← A[q+1..r]
    int n1 = q - p + 1;
    int n2 = r - q;

    int L[n1], M[n2];
    int L2[n1], M2[n2];

    cont += 2;
    for (int i = 0; i < n1; i++)
    {
        cont += 7;
        L[i] = conexiones[p + i];
        L2[i] = indices[p + i];
    }
    cont += 2;
    for (int j = 0; j < n2; j++)
    {
        cont += 9;
        M[j] = conexiones[q + 1 + j];
        M2[j] = indices[q + 1 + j];
    }

    // Maintain current index of sub-arrays and main array
    int i, j, k;
    i = 0;
    j = 0;
    k = p;
    cont += 5;
    // Until we reach either end of either L or M, pick larger among
    // elements L and M and place them in the correct position at A[p..r]
    while (i < n1 && j < n2)
    {
        cont += 9;
        if (L[i] >= M[j])
        {
            conexiones[k] = L[i];
            indices[k] = L2[i];
            i++;
        }
        else
        {
            conexiones[k] = M[j];
            indices[k] = M2[j];
            j++;
        }
        k++;
    }

    // When we run out of elements in either L or M,
    // pick up the remaining elements and put in A[p..r]
    cont += 1;
    while (i < n1)
    {
        cont += 7;
        conexiones[k] = L[i];
        indices[k] = L2[i];
        i++;
        k++;
    }
    cont += 1;
    while (j < n2)
    {
        cont += 7;
        conexiones[k] = M[j];
        indices[k] = M2[j];
        j++;
        k++;
    }
}

/**
 * If the array is not empty, divide it into two subarrays, sort them and merge them
 *
 * @param l left index of the subarray of arr to be sorted
 * @param r the rightmost index of the array
 */
void mergeSort(int l, int r)
{
    cont += 1;
    if (l < r)
    {
        cont += 8;
        // m is the point where the array is divided into two subarrays
        int m = l + (r - l) / 2;

        mergeSort(l, m);
        mergeSort(m + 1, r);

        // Merge the sorted subarrays
        merge(l, m, r);
    }
}

/**
 * It prints the array.
 *
 * @param size The size of the array
 */
void printArray(int size)
{
    for (int i = 0; i < size; i++)
        printf("%d ", conexiones[i]);
    printf("\n");
    for (int i = 0; i < size; i++)
        printf("%d ", indices[i]);
    printf("\n");
}

/**
 * It counts the number of connections each node has
 *
 * @param nodos number of nodes
 */
void contar_conexiones(int nodos)
{
    cont += 2;
    for (int i = 0; i < nodos; i++)
    {
        cont += 4;
        conexiones[i] = 0;
    }
    cont += 5;
    for (int i = 0; i < nodos - 1; i++)
    {
        cont += 8;
        indices[i] = i;
        for (int j = i + 1; j < nodos; j++)
        {
            cont += 5;
            if (matriz[i][j] == 1 && i != j)
            {
                cont += 4;
                conexiones[i]++;
                conexiones[j]++;
            }
        }
    }
    indices[nodos - 1] = nodos - 1;
}

/**
 * It creates a graph with a given number of nodes and a given number of arcs per node
 *
 * @param nodos number of nodes
 * @param arcospornodo the number of arcs per node
 */
void construye_grafo(int nodos, double arcospornodo)
{
    int arcos;

    arcos = nodos * arcospornodo;
    crear_grafo(nodos, arcos); // crea grafo maximo
    // imprimir_grafo(nodos);
}

/* funcion recursiva para generar todas las asignaciones posibles.
Cuando llega al nodo hoja llama a comprueba_conflictos para
contabilizar si se trata de una soluci�n v�lida o fallida          */
/* Se ha realizado de forma recursiva como paso previo para usar backtracking */

/**
 * It assigns colors to a graph and counts the number of solutions with and without conflicts
 *
 * @param ind index of the node to be colored
 * @param nodos number of nodes
 * @param k number of colors
 */
void asigna_colores(int ind, int nodos, int k)
{ // T(n)= 1+ max(1+LLAMADA_CONFLICTOS+1+2; 2+k(3+3+LLAMADA_ASIGNAR))
    int j;
    unsigned long long cf;
    cont += 1;
    if (ind >= nodos)
    {
        cf = comprueba_conflictos(nodos);
        if (cf == 0)
        {
            sin_conflictos++;
        }
        else
        {
            con_conflictos++;
        }
        cont += 4;
    }
    else
    {
        cont += 2;
        for (j = 0; j < k; j++)
        {
            cont += 6;
            asignaciones[ind] = j;
            asigna_colores(ind + 1, nodos, k);
        }
    }
}

void asigna_colores_backtracking(int ind, int nodos, int k)
{
    // T(n) = 1 + max(1 + n^2 + 1 + 2; 2 + 3(3+ n + 7 + T(n-1)))
    int j;
    unsigned long long cf;
    cont += 1;
    if (ind >= nodos)
    {
        cf = comprueba_conflictos(nodos);
        if (cf == 0)
        {
            sin_conflictos++;
        }
        else
        {
            con_conflictos++;
        }
        cont += 4;
    }
    else
    {
        cont += 2;
        for (j = 0; j < k; j++)
        {
            cont += 6;
            asignaciones[ind] = j;
            // printf("Asigno a %d el color %d\n", ind, j);
            if (!comprueba_adyacencia(ind, nodos))
            {
                cont += 2;
                asigna_colores_backtracking(ind + 1, nodos, k);
            }
            cont += 1;
            if (sin_conflictos >= 1)
                return; // Ha ocurrido la primera asignacion completa sin conflicto, acabará de volver de la condición
            // base de la recursión.
            cont += 1;
            asignaciones[ind] = -1;
        }
    }
}

void asigna_colores_backtracking_todas(int ind, int nodos, int k)
{
    int j;
    unsigned long long cf;
    cont += 1;
    if (ind >= nodos)
    {
        cf = comprueba_conflictos(nodos);
        if (cf == 0)
        {
            sin_conflictos++;
        }
        else
        {
            con_conflictos++;
        }
        cont += 4;
    }
    else
    {
        cont += 2;
        for (j = 0; j < k; j++)
        {
            cont += 6;
            asignaciones[indices[ind]] = j;
            /*printf("Asigno a %d el color %d\n", indices[ind], j);
            for(int i = 0; i < nodos; i++){
              printf("%d\t", asignaciones[i]);
            }
            printf("\n");*/
            if (!comprueba_adyacencia(indices[ind], nodos))
            {
                cont += 2;
                asigna_colores_backtracking_todas(ind + 1, nodos, k);
            }          /*else {
                 printf("Fallo %lld\n", con_conflictos);
               }*/
            cont += 1; // SI SE QUIERE TODAS COMENTAR EL IF Y EL CONT
            if (sin_conflictos >= 1)
                return; // Ha ocurrido la primera asignacion completa sin conflicto, acabará de volver de la condición
            // base de la recursión.
            cont += 1;
            asignaciones[indices[ind]] = -1;
        }
    }
}

/**
 * It creates a graph with 17 nodes and a number of arcs that varies from 17*16/2 to 0. Then it assigns
 * colors to the nodes and prints the number of solutions, the number of failed attempts, the total
 * number of attempts, the proportion of solutions to failed attempts, the time it took to run the
 * function, and the number of witnesses
 *
 * @param k number of colors
 */
void prueba_densidad(int k)
{
    double proporcion;
    clock_t start;
    clock_t end;
    double cpu_time_used;

    int nodos = 100;
    int arcos = nodos * (nodos - 1) / 2;

    crear_grafo(nodos, arcos);
    for (; arcos >= 0; arcos -= 5)
    {
        sin_conflictos = 0L;
        con_conflictos = 0L;
        cont = 0;

        contar_conexiones(nodos);
        mergeSort(0, nodos - 1); // De 0 a NODOS-1
        cont = 0;                // No contar en el analisis

        for (int i = 0; i < nodos; i++)
        { // Control de retroceso en asignaciónes, -1, no asignado aun
            asignaciones[i] = -1;
        }

        start = clock();
        asigna_colores_backtracking_todas(0, nodos, k);
        end = clock();

        // imprimir_grafo (nodos) ;	// si se genera primera solucion

        proporcion = (double)sin_conflictos;
        proporcion /= (double)con_conflictos;
        cpu_time_used = ((double)(end - start)) / CLOCKS_PER_SEC;

        printf("Nodos %d\t Arcos %3d\t Soluciones %llu\t Fallidas %llu\t Total %llu\t Proporcion %lf\t Tiempo %3f\t "
               "Testigos %llu \n",
               nodos, arcos, sin_conflictos, con_conflictos, sin_conflictos + con_conflictos, proporcion, cpu_time_used,
               cont);
        borrar_arco_aleatorio(nodos, arcos);
        borrar_arco_aleatorio(nodos, arcos);
        borrar_arco_aleatorio(nodos, arcos);
        borrar_arco_aleatorio(nodos, arcos);
        borrar_arco_aleatorio(nodos, arcos);
    }
}

/* funci�n que construye un grafo de 100 nodos con una cantidad de arcos
determinada por arcospornodo
Posteriormente comienza a analizar la colorabilidad de subgrafos
de tama�o 10, 11, .., hasta 100.
De esa forma la proporcion de arcos por nodo es aproximadamente la que se esperaba
aunque hay variabilidad.
Se cronometra la duracion de la generacion exhaustiva de todas las asignaciones de color poisbles
sean v�lidas o no  */

/**
 * It generates a graph with a given number of nodes and edges per node, and then tries to color the
 * graph with a given number of colors
 *
 * @param k number of colors
 * @param arcospornodo the number of edges per node
 */
void explora_k_colorabilidad(int k, double arcospornodo)
{
    int nodos;
    double proporcion;

    clock_t start;
    clock_t end;
    double cpu_time_used;

    construye_grafo(100, arcospornodo);

    for (nodos = 23; nodos < 24; nodos++)
    {
        sin_conflictos = 0L;
        con_conflictos = 0L;
        cont = 0;

        start = clock();
        asigna_colores(0, nodos, k);
        end = clock();

        // imprimir_grafo (nodos) ;	// si se genera primera solucion

        proporcion = (double)sin_conflictos;
        proporcion /= (double)con_conflictos;
        cpu_time_used = ((double)(end - start)) / CLOCKS_PER_SEC;

        printf("Nodos %3d\t Soluciones %llu\t Fallidas %llu\t Total %llu\t Proporcion %lf\t Tiempo %3f\t Testigos %llu \n", nodos, sin_conflictos, con_conflictos, sin_conflictos + con_conflictos, proporcion, cpu_time_used, cont);
    }
}

void explora_k_colorabilidad_backtracking(int k, double arcospornodo)
{
    int nodos;
    double proporcion;

    clock_t start;
    clock_t end;
    double cpu_time_used;

    construye_grafo(2000, arcospornodo);

    for (nodos = 0; nodos < 2000; nodos += 1)
    {
        sin_conflictos = 0L;
        con_conflictos = 0L;
        cont = 0;

        for (int i = 0; i < nodos; i++)
        { // Control de retroceso en asignaciónes, -1, no asignado aun
            asignaciones[i] = -1;
        }

        start = clock();
        asigna_colores_backtracking(0, nodos, k);
        end = clock();

        // imprimir_grafo (nodos) ;	// si se genera primera solucion

        proporcion = (double)sin_conflictos;
        proporcion /= (double)con_conflictos;
        cpu_time_used = ((double)(end - start)) / CLOCKS_PER_SEC;

        printf("Nodos %3d\t Soluciones %llu\t Fallidas %llu\t Total %llu\t Proporcion %lf\t Tiempo %3f\t Testigos %llu \n", nodos, sin_conflictos, con_conflictos, sin_conflictos + con_conflictos, proporcion, cpu_time_used, cont);
    }
}

void generar_4_cliques(int nodos)
{
    inicializar_grafo(nodos);
    for (int i = 3; i < nodos; i += 3)
    {
        matriz[i][i - 1] = 1;
        matriz[i - 1][i] = 1;

        matriz[i][i - 2] = 1;
        matriz[i - 2][i] = 1;

        matriz[i - 2][i - 1] = 1;
        matriz[i - 1][i - 2] = 1;

        matriz[i - 3][i - 1] = 1;
        matriz[i - 1][i - 3] = 1;

        matriz[i - 2][i - 3] = 1;
        matriz[i - 3][i - 2] = 1;

        matriz[i][i - 3] = 1;
        matriz[i - 3][i] = 1;
    }
}

void generar_3_cliques(int nodos)
{
    inicializar_grafo(nodos);
    for (int i = 2; i < nodos; i += 2)
    {
        matriz[i][i - 1] = 1;
        matriz[i - 1][i] = 1;

        matriz[i][i - 2] = 1;
        matriz[i - 2][i] = 1;

        matriz[i - 2][i - 1] = 1;
        matriz[i - 1][i - 2] = 1;
    }
}

void explora_k_colorabilidad_backtracking_todas(int k, double arcospornodo, int mnodos, int step)
{
    int nodos;
    double proporcion;

    clock_t start;
    clock_t end;
    double cpu_time_used;

    construye_grafo(mnodos, arcospornodo);
    // generar_4_cliques(mnodos);
    // generar_3_cliques(mnodos);

    for (nodos = 1; nodos <= mnodos; nodos += step)
    {
        sin_conflictos = 0L;
        con_conflictos = 0L;

        contar_conexiones(nodos);
        mergeSort(0, nodos - 1); // De 0 a NODOS-1
        cont = 0;                // No contar en el analisis

        for (int i = 0; i < nodos; i++)
        { // Control de retroceso en asignaciónes, -1, no asignado aun
            asignaciones[i] = -1;
        }

        start = clock();
        asigna_colores_backtracking_todas(0, nodos, k);
        end = clock();

        // imprimir_grafo (nodos) ;	// si se genera primera solucion

        proporcion = (double)sin_conflictos;
        proporcion /= (double)con_conflictos;
        cpu_time_used = ((double)(end - start)) / CLOCKS_PER_SEC;

        printf("Nodos %3d\t Soluciones %llu\t Fallidas %llu\t Total %llu\t Proporcion %lf\t Tiempo %3f\t Testigos %llu \n", nodos, sin_conflictos, con_conflictos, sin_conflictos + con_conflictos, proporcion, cpu_time_used, cont);
    }
}

int main(void)
{
    srand(3);

    crear_matriz();
    /*
    for (int i = 1; i < 11; i++) {
      for (int j = 1; j <= i; j++) {
        clusters[i] = -1;
      }
      cont = 0;
      // comprueba_conflictos(i);
      asigna_colores(0, i, 3);
      printf("%d\n", cont);
    }
    */
    // explora_k_colorabilidad(3, 0); // exploramos con k=3 y 0 arcos por nodo
    // prueba_densidad(3);
    // explora_k_colorabilidad_backtracking_todas(3, 2000 * 1999 / 2 / 2000); // exploramos con k=3 y 0 arcos por nodo
    int n = 103;
    explora_k_colorabilidad_backtracking_todas(2, (.05 * n * (n - 1) / 2) / n, n, 3);
    // explora_k_colorabilidad_backtracking(3, 100*99/2/100); // exploramos con k=3 y completo

    for (int i = 0; i < MAX_NODOS; i++)
    {
        free(matriz[i]);
    }

    exit(0);
}