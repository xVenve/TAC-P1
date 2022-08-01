def imprimir(c, h, st):
    s = ""
    for it in range(0, len(c)):
        if h == -1 and it == 0:
            s = s + "()"
        if it == h:
            s = s + "(" + str(c[h]) + ")"
        else:
            s = s + str(c[it])
        if h == len(c) and it == len(c) - 1:
            s = s + "()"
    print(s + "\t" + str(st))


def ej2aIterativo(mx, paso, show):
    co = ["$", 1]
    for i in range(0, mx, paso):
        c = co.copy()
        ej2a(c, show)
        co.append(1)


def ej2a(cint, show):
    cabeza = 0
    steps = 0
    salir = False

    if show:
        imprimir(cint, cabeza, steps)
    while salir is False:
        while salir is False:
            if len(cint) == cabeza or cabeza == -1:
                cabeza = cabeza - 1
                steps = steps + 1
                if show:
                    imprimir(cint, cabeza, steps)
                break
            elif cint[cabeza] == "$" or cint[cabeza] == 0 or cint[cabeza] == 1:
                cabeza = cabeza + 1
                steps = steps + 1
                if show:
                    imprimir(cint, cabeza, steps)

        while salir is False:
            if cint[cabeza] == 0:
                cint[cabeza] = 1
                cabeza = cabeza - 1
                steps = steps + 1
                if show:
                    imprimir(cint, cabeza, steps)
            if cint[cabeza] == 1:
                cint[cabeza] = 0
                cabeza = cabeza - 1
                steps = steps + 1
                if show:
                    imprimir(cint, cabeza, steps)
                break
            if cint[cabeza] == "$":
                cabeza = cabeza + 1
                steps = steps + 1
                if show:
                    imprimir(cint, cabeza, steps)
                while salir is False:
                    if len(cint) == cabeza or cabeza == -1:
                        cabeza = cabeza - 1
                        steps = steps + 1
                        if show:
                            imprimir(cint, cabeza, steps)
                        break
                    elif cint[cabeza] == 1:
                        cint[cabeza] = 1
                        cabeza = cabeza + 1
                        steps = steps + 1
                        if show:
                            imprimir(cint, cabeza, steps)
                    elif cint[cabeza] == 0:
                        cint[cabeza] = 0
                        cabeza = cabeza + 1
                        steps = steps + 1
                        if show:
                            imprimir(cint, cabeza, steps)

                while salir is False:
                    if cint[cabeza] == 1:
                        cint.pop(cabeza)
                        cabeza = cabeza - 1
                        steps = steps + 1
                        if show:
                            imprimir(cint, cabeza, steps)
                    if cint[cabeza] == 0:
                        cint.pop(cabeza)
                        cabeza = cabeza - 1
                        steps = steps + 1
                        if show:
                            imprimir(cint, cabeza, steps)
                    if cint[cabeza] == "$":
                        cint.pop(cabeza)
                        cabeza = cabeza - 1
                        steps = steps + 1
                        if show:
                            imprimir(cint, cabeza, steps)
                        break
                while salir is False:
                    if len(cint) == cabeza or cabeza == -1:
                        cabeza = cabeza + 1
                        steps = steps + 1
                        if show:
                            imprimir(cint, cabeza, steps)
                        print(str(cint) + " " + str(len(cint)) + " " + str(steps))
                        salir = True
                    elif cint[cabeza] == 1:
                        cint[cabeza] = 1
                        cabeza = cabeza - 1
                        steps = steps + 1
                        if show:
                            imprimir(cint, cabeza, steps)
                    elif cint[cabeza] == 0:
                        cint[cabeza] = 0
                        cabeza = cabeza - 1
                        steps = steps + 1
                        if show:
                            imprimir(cint, cabeza, steps)

        while salir is False:
            if cint[cabeza] == 1:
                cint[cabeza] = 1
                cabeza = cabeza - 1
                steps = steps + 1
                if show:
                    imprimir(cint, cabeza, steps)
            if cint[cabeza] == 0:
                cint[cabeza] = 0
                cabeza = cabeza - 1
                steps = steps + 1
                if show:
                    imprimir(cint, cabeza, steps)
            if cint[cabeza] == "$":
                cabeza = cabeza - 1
                steps = steps + 1
                if show:
                    imprimir(cint, cabeza, steps)
                break
        if len(cint) == cabeza or cabeza == -1:
            cint.insert(0, 1)
            cabeza = cabeza + 2
            steps = steps + 1
            if show:
                imprimir(cint, cabeza, steps)
        elif cint[cabeza] == 0:
            cint[cabeza] = 1
            cabeza = cabeza + 1
            steps = steps + 1
            if show:
                imprimir(cint, cabeza, steps)
        elif cint[cabeza] == 1:
            cint[cabeza] = 0
            cabeza = cabeza - 1
            steps = steps + 1
            if show and salir is False:
                imprimir(cint, cabeza, steps)
            while salir is False:
                if len(cint) == cabeza or cabeza == -1:
                    cint.insert(0, 1)
                    cabeza = cabeza + 2
                    steps = steps + 1
                    if show:
                        imprimir(cint, cabeza, steps)
                    break
                elif cint[cabeza] == 1:
                    cint[cabeza] = 0
                    cabeza = cabeza - 1
                    steps = steps + 1
                    if show:
                        imprimir(cint, cabeza, steps)
                elif cint[cabeza] == 0:
                    cint[cabeza] = 1
                    cabeza = cabeza + 1
                    steps = steps + 1
                    if show:
                        imprimir(cint, cabeza, steps)
                    break


if __name__ == "__main__":
    cinta = [1, 0, 0, 0, '$', 1, 0, 1, 0, 1, 0, 1]
    ej2a(cinta, True)

    ej2aIterativo(10, 1, False)
