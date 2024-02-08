import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String target = "To be or not to be that is the question";
        int population_size = 2500;
        double mutation_rate = 0.05;

        Population population = new Population(target, population_size, mutation_rate);
        int generation = 1;
        while (true) {
            population.evaluate_fitness();

            Element highest = population.getHighestElement();
            System.out.println(highest + " Generation: " + generation++);
            if (highest.fitness == target.length())
                return;

            population.createSelectionPool();
            population.reproduction();
        }

    }
}

class Population {
    String target;
    int population_size;
    double mutation_rate;

    Element[] population;

    public Population(String target, int population_size, double mutation_rate) {
        this.target = target;
        this.population_size = population_size;
        this.mutation_rate = mutation_rate;
        initPopulation();
    }

    private void initPopulation() {
        RandomSentenceCreator random = new RandomSentenceCreator();

        population = new Element[population_size];
        for (int i = 0; i < population_size; i++) {
            population[i] = new Element(random.getRandomSentence(target.length()));
        }
    }

    public void evaluate_fitness() {
        for (int i = 0; i < population_size; i++) {
            Element element = population[i];
            element.fitness = getFitness(element);
        }
    }

    private int getFitness(Element element) {
        int fitness = 0;
        for (int i = 0; i < target.length(); i++) {
            if (target.charAt(i) == element.string.charAt(i))
                fitness++;
        }
        return fitness;
    }

    public Element getHighestElement() {
        Element element = new Element("");
        element.fitness = -1;

        for (int i = 0; i < population_size; i++) {
            if (population[i].fitness > element.fitness)
                element = population[i];
        }
        return element;
    }

    List<Element> selectionPool;
    public void createSelectionPool() {
        selectionPool = new ArrayList<>();
        for (int i = 0; i < population_size; i++) {
            Element element = population[i];
            for (int j = 0; j < element.fitness; j++) {
                selectionPool.add(element);
            }
        }
    }

    public void reproduction() {
        // Replace the old population with new children
        for (int i = 0; i < population_size; i++) {
            // Pick 2 parents
            Element parent1 = selectionPool.get((int) (Math.random() * selectionPool.size()));
            Element parent2 = selectionPool.get((int) (Math.random() * selectionPool.size()));

            Element child = crossover(parent1, parent2);
            if (Math.random() < mutation_rate)
                child.mutate();

            population[i] = child;
        }
    }

    private Element crossover(Element parent1, Element parent2) {
        Element child = new Element("");

        Element highest, lowest;
        if (parent1.fitness > parent2.fitness) {
            highest = parent1;
            lowest = parent2;
        } else {
            highest = parent2;
            lowest = parent1;
        }

        double chance = highest.fitness / (highest.fitness + lowest.fitness);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < highest.string.length(); i++) {
            if (Math.random() < chance)
                sb.append(highest.string.charAt(i));
            else
                sb.append(lowest.string.charAt(i));
        }
        child.string = sb.toString();
        return child;
    }
}

class Element {
    String string;
    double fitness;

    public Element(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string + ":" + fitness;
    }

    public void mutate() {
        RandomSentenceCreator r = new RandomSentenceCreator();
        String random = r.getRandomSentence(this.string.length());

        char[] newString = this.string.toCharArray();

        // Mutate 3 letters
        newString[(int) (Math.random() * newString.length)] = random.charAt((int) (Math.random() * random.length()));
        newString[(int) (Math.random() * newString.length)] = random.charAt((int) (Math.random() * random.length()));
        newString[(int) (Math.random() * newString.length)] = random.charAt((int) (Math.random() * random.length()));

        this.string = String.valueOf(newString);
    }
}

class RandomSentenceCreator {
    private final char[] characters;

    public RandomSentenceCreator() {
        // Initialize to all letters upper and lowercase, space and dot.
        this.characters = new char[26 + 26 + 1 + 1];
        for (int i = 'a'; i <= 'z'; i++) {
            characters[i - 'a'] = (char) i;
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            characters[i - 'A' + 26] = (char) i;
        }
        characters[26 + 26] = ' ';
        characters[26 + 26 + 1] = '.';
    }

    public String getRandomSentence(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(characters[(int) (Math.random() * characters.length)]);
        }
        return sb.toString();
    }
}