# 🎮 Tetris em Java

## Como jogar no VSCode

### Pré-requisitos
- **Java JDK 11+** instalado ([download](https://adoptium.net/))
- **VSCode** com a extensão **"Extension Pack for Java"** instalada

### Como rodar
1. Abra a pasta `Tetris` no VSCode: `File > Open Folder...`
2. Aguarde o VSCode indexar o projeto Java (barra de status)
3. Pressione **F5** ou vá em `Run > Start Debugging`
4. O jogo abrirá em uma janela separada!

### Alternativa (Terminal)
```bash
cd Tetris
mkdir -p bin
javac -d bin src/*.java
java -cp bin Tetris
```

---

## Controles

| Tecla         | Ação                   |
|---------------|------------------------|
| ← →           | Mover peça             |
| ↑             | Girar (horário)        |
| Z             | Girar (anti-horário)   |
| ↓             | Descer rápido          |
| SPACE         | Hard drop (cair direto)|
| C ou SHIFT    | Segurar peça (Hold)    |
| P             | Pausar / Continuar     |
| R             | Reiniciar (Game Over)  |

---

## Funcionalidades

- ✅ Todas as 7 peças oficiais (I, O, T, S, Z, J, L)
- ✅ Sistema de rotação com wall kick
- ✅ Ghost piece (sombra da peça)
- ✅ Hold piece (guardar peça)
- ✅ Preview de 3 próximas peças
- ✅ Hard drop e Soft drop
- ✅ Sistema de pontuação oficial
- ✅ Aumento de dificuldade por nível (a cada 10 linhas)
- ✅ Contador de linhas, nível e score
- ✅ Tela de pausa e game over
