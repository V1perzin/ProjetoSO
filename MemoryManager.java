import java.util.ArrayList;
import java.util.List;

/* ArrayList e List são importados para armazenar os blocos de memória dinamicamente */

class MemoryBlock {
    int start;
    int size;
    boolean allocated;
    
    public MemoryBlock(int start, int size, boolean allocated) {
        this.start = start;
        this.size = size;
        this.allocated = allocated;
    }
}

/* Um MemoryBlock é representado por 
start: endereço inicial do bloco
size: tamanho do bloco
allocated: ele indica se estiver alocado(true) ou livre(false)
 */

public class MemoryManager {
    private static final int MEMORY_SIZE = 128; // 128 KB
    private List<MemoryBlock> memory;
    private int lastAllocatedIndex = 0; // Para Next Fit

/* Gerencia a memória de 128KB, com 1KB por bloco 
memory: lista de blocos de memória
lastAllocatedIndex: Última posição alocada (usado no Next Fit)
*/

    public MemoryManager() {
        memory = new ArrayList<>();
        memory.add(new MemoryBlock(0, MEMORY_SIZE, false)); // Memória inicial livre
    }

/* Cria um bloco livre de 128KB na memória */
    
    public boolean allocateFirstFit(int size) {
        for (MemoryBlock block : memory) {
            if (!block.allocated && block.size >= size) {
                allocateBlock(block, size);
                return true;
            }
        }
        return false; // Sem espaço suficiente
    }

/* Percorre a lista e pega o primeiro bloco livre que caiba no tamanho solicitado.
Se encontrar, aloca o bloco e retorna true.
Se não encontrar, retorna false.
*/

    public boolean allocateNextFit(int size) {
        int startIndex = lastAllocatedIndex;
        for (int i = 0; i < memory.size(); i++) {
            int index = (startIndex + i) % memory.size();
            MemoryBlock block = memory.get(index);
            if (!block.allocated && block.size >= size) {
                allocateBlock(block, size);
                lastAllocatedIndex = index;
                return true;
            }
        }
        return false;
    }

/* Começa a busca a partir da última alocação.
Continua percorrendo a lista circularmente até encontrar um espaço adequado.
Se alocar, atualiza lastAllocatedIndex para começar a próxima busca dali. 
*/

    public boolean allocateBestFit(int size) {
        MemoryBlock bestBlock = null;
        for (MemoryBlock block : memory) {
            if (!block.allocated && block.size >= size) {
                if (bestBlock == null || block.size < bestBlock.size) {
                    bestBlock = block;
                }
            }
        }
        if (bestBlock != null) {
            allocateBlock(bestBlock, size);
            return true;
        }
        return false;
    }

/* Procura o menor bloco livre possível que caiba no processo (minimizando fragmentação).
Se encontrar, aloca o menor bloco disponível. 
*/

    private void allocateBlock(MemoryBlock block, int size) {
        if (block.size > size) {
            memory.add(memory.indexOf(block) + 1, new MemoryBlock(block.start + size, block.size - size, false));
        }
        block.size = size;
        block.allocated = true;
    }

/* Divide o bloco grande em dois se houver espaço extra após a alocação.
Marca o bloco como alocado.
*/
    
    public void deallocate(int startAddress) {
        for (MemoryBlock block : memory) {
            if (block.start == startAddress) {
                block.allocated = false;
                mergeFreeBlocks();
                return;
            }
        }
    }

/* Desaloca um bloco quando recebe seu endereço inicial.
Chama mergeFreeBlocks para unir blocos livres adjacentes.
*/
    
    private void mergeFreeBlocks() {
        for (int i = 0; i < memory.size() - 1; i++) {
            if (!memory.get(i).allocated && !memory.get(i + 1).allocated) {
                memory.get(i).size += memory.get(i + 1).size;
                memory.remove(i + 1);
                i--;
            }
        }
    }

/* Verifica blocos livres consecutivos e os une para reduzir fragmentação. */
    
    public void printMemory() {
        for (MemoryBlock block : memory) {
            System.out.println("Start: " + block.start + " KB, Size: " + block.size + " KB, " + (block.allocated ? "Allocated" : "Free"));
        }
    }

/* Exibe a memória, mostrando quais blocos estão alocados ou livres. */
    
    public static void main(String[] args) {
        MemoryManager manager = new MemoryManager();
        manager.allocateFirstFit(30);
        manager.allocateNextFit(50);
        manager.allocateBestFit(20);
        manager.printMemory();
        manager.deallocate(0);
        manager.printMemory();
    }
}

/* 
Cria o gerenciador de memória.
Aloca 30 KB usando First Fit.
Aloca 50 KB usando Next Fit.
Aloca 20 KB usando Best Fit.
Imprime o estado da memória.
Desaloca o primeiro bloco e imprime novamente.
 */



 /* Representação da memória: Lista dinâmica de blocos.
Três estratégias de alocação:
First Fit: Primeiro bloco disponível.
Next Fit: Começa do último alocado.
Best Fit: Melhor encaixe possível.
Desalocação e fusão de blocos livres para evitar fragmentação.
Saída formatada para visualização da memória. */