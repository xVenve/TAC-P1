#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define MAX_NODOS  2000
unsigned long long cont = 0;

int *matriz [MAX_NODOS] ;

int listaClique [MAX_NODOS];  // Lista donde van los nodos candidatos a formar un k-clique
int asignaciones[MAX_NODOS];
int conexiones[MAX_NODOS];
int indices[MAX_NODOS];
unsigned long long int posibles_asignaciones;

char *mi_malloc (int nbytes) {
    char *p ;
    static long int nb = 0L ;
    static int nv = 0 ;

    p = malloc (nbytes) ;
    if (p == NULL) {
        fprintf (stderr, "Error, no queda memoria disponible para %d bytes mas\n", nbytes) ;
        fprintf (stderr, "Se han reservado %ld bytes en %d llamadas\n", nb, nv) ;
        exit (0) ;
    }

    nb += (long) nbytes ;
    nv++ ;

    return p ;
}

/* crea matriz: vector de punteros a vectores en memoria din�mica */
void crear_matriz () {
    int i ;

    for (i = 0 ; i < MAX_NODOS ; i++) {
        matriz [i] = (int *) mi_malloc (sizeof (int)*MAX_NODOS) ;
    }
}

void liberar_matriz () {
    for (int i = 0; i < MAX_NODOS; i++) {
        free(matriz[i]);
    }
}

/* 0 indica que no hay arista entre los nodos i-j */
void inicializar_grafo (int nodos) {
    int i ;
    int j ;

    for (i = 0 ; i < nodos ; i++) {
        for (j = 0 ; j < nodos ; j++) {
            matriz [i][j] = 0 ;
        }
    }
}

void resetear_clique(int nodos) {
    // Resetea lista de cliques
    for(int i = 0; i < nodos; i++) {
        listaClique[i] = -1;  // Resetea nodos de posibles cliques
    }
}

/* crea grafo con n nodos y a arcos: no se controlan los limites */
/* 1 indica que hay arista entre los nodos i-j */
void crear_grafo (int nodos, int arcos) {
    int i ;
    int p ;
    int q ;

    inicializar_grafo (nodos) ;

    for (i = 0 ; i < arcos ; i++) {
        do {
            p = rand () % nodos ;
            q = rand () % nodos ;
        } while (p == q || matriz [p][q] != 0) ; // evitar diagonal y arcos existentes
        matriz [p][q] = 1 ;
        matriz [q][p] = 1 ; 	// arista sim�trica
    }
}

void imprimir_grafo (int nodos) {
    for (int i=0; i<nodos; i++) {
        printf("Nodo %d: ", i) ;
        for (int j=0; j<nodos; j++){
            if (matriz[i][j])
                printf("%d, ", j) ;
        }
        printf("\n ");
    }
}

void construye_grafo (int nodos, double arcospornodo) {
    int arcos ;

    arcos = nodos * arcospornodo ;
    crear_grafo (nodos, arcos) ;	// crea grafo maximo
}

/**
 * It counts the number of connections each node has
 *
 * @param nodos number of nodes
 */
void contar_conexiones(int nodos) {
    cont += 2;
    for (int i = 0; i < nodos; i++) {
        cont += 4;
        conexiones[i] = 0;
    }
    cont += 5;
    for (int i = 0; i < nodos - 1; i++) {
        cont += 8;
        indices[i] = i;
        for (int j = i + 1; j < nodos; j++) {
            cont += 5;
            if (matriz[i][j] == 1 && i != j) {
                cont += 4;
                conexiones[i]++;
                conexiones[j]++;
            }
        }
    }
    indices[nodos - 1] = nodos - 1;
}

/*
 * Para cada nodo de la lista de candidatos a clique,
 * comprueba que esté unido al resto de elementos.
 */
int esClique(int iListaClique) { // K^2-K
    cont+=2;
    for (int i = 0; i < iListaClique; i++) { //max K
        cont+=6;
        for (int j = i + 1; j < iListaClique; j++) { // max K-1
            cont += 4;
            if (matriz[listaClique[i]][listaClique[j]] == 0)
                return 0;
        }
    }
    return 1;
}

/*
 * Este método recursivo busca un k-clique.
 * Devuelve 1 si hay al menos 1 k-clique.
 * nodos: numero de nodos.
 * n: nodo actual (empieza en 0)
 * iListaClique: posición de la lista de clique
 * k: numero de colores
 * return: 1 si hay k-clique, 0 si no lo hay
 */
int encontrarK_clique(int nodos, int n, int iListaClique, int k) { //numero de nodos, nodo actual (empieza en 0)
    // Para cada nodo no explorado, busca vecinos con los que hacer cliques
    cont+=2;
    for (int j = n; j < nodos; j++){
        cont+=5;
        // Comprueba que no haya sido descartado previamente
        //Heurística
        if(conexiones[j]>k-2) {
            // Añade el nodo a la lista de candidatos a clique en la posición correspondiente
            listaClique[iListaClique] = j;
            cont+=3;
            // comprueba si los nodos de la lista de candidatos forman clique entre ellos
            if (esClique(iListaClique + 1)){
                cont+=2;
                // Si el clique es menor a K, busca más candidatos
                if (iListaClique < k - 1){
                    cont+=5;
                    // La búsqueda devolverá 0 si no hay clique, 1 si encuentra
                    int clique = encontrarK_clique(nodos, j + 1, iListaClique + 1, k);
                    // Si se encuentra clique en la recursión, termina.
                    if (clique)
                        return 1 ;
                } else{
                    // Si encuentra k-clique, notifica los nodos y termina.
                    //printf("Encontrado %d-clique: ", k);
                    //for (int kk=0; kk<k; kk++){
                    //    printf("%d, ", listaClique[kk]);
                    //}
                    //printf("\n");
                    return 1 ;
                }
            }
        }
    }
    return 0 ;
}



/* funci�n que construye un grafo de un número nodos con una cantidad de arcos
determinada por arcospornodo
Posteriormente comienza a analizar la colorabilidad de subgrafos
de tama�o 10, 11, .., hasta 100.
De esa forma la proporcion de arcos por nodo es aproximadamente la que se esperaba
aunque hay variabilidad.
Se cronometra la duracion de la generacion exhaustiva de todas las asignaciones de color poisbles
sean v�lidas o no  */
void explora_k_colorabilidad (int k, double arcospornodo,int nodes) {
    int nodos ;
    construye_grafo (nodes, arcospornodo) ;
    contar_conexiones(nodes);
    for (nodos = 10 ; nodos < nodes ; nodos++) {
        resetear_clique(nodos);
        //printf("Nodos %d:\n", nodos) ;
        encontrarK_clique(nodos, 0, 0, k + 1) ;
    }
}
void pruebas(int nodes){
    clock_t start;
    clock_t end;
    double cpu_time_used;
    crear_matriz() ;
    cont = 0;
    start = clock();
    explora_k_colorabilidad (3, 4.0,nodes) ; // exploramos con k=3 y 4 arcos por nodo
    end = clock();
    cpu_time_used = ((double) (end - start)) / CLOCKS_PER_SEC;
    printf("Nodos %d\t Tiempo %3f\t Testigos %llu\n",nodes,cpu_time_used, cont);
    liberar_matriz() ;
}

int main (void)
{
    srand (3);
    for (int no = 0 ; no < 200 ; no++) {
        pruebas(no*10);
    }

}