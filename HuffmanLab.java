/**
 * CMSC204 Huffman Lab
 * Author: G. Araya
 * Date: May 4, 2025
 *
 * Description:
 * Builds a Huffman Tree, encodes/decodes strings, and follows all rules:
 * 1) Alphabetical tie-breaking
 * 2) Space before other characters
 * 3) Lower frequency gets higher priority
 */

package application;

import java.util.*;

class HuffmanNode {
    char ch;
    int freq;
    HuffmanNode left, right;

    HuffmanNode(char ch, int freq) {
        this.ch = ch;
        this.freq = freq;
    }

    boolean isLeaf() {
        return left == null && right == null;
    }
}

class HuffmanComparator implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode a, HuffmanNode b) {
        if (a.freq == b.freq) {
            if (a.ch == ' ') return -1;
            if (b.ch == ' ') return 1;
            return Character.compare(a.ch, b.ch);
        }
        return Integer.compare(a.freq, b.freq);
    }
}

public class HuffmanLab {

    static Map<Character, String> huffmanCodes = new HashMap<>();
    static HuffmanNode root;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Step 1: Use default phrase first
        String defaultPhrase = "create a huffman tree";
        System.out.println("Default phrase: \"" + defaultPhrase + "\"\n");
        processPhrase(defaultPhrase);

        while (true) {
            System.out.println("\nWhat would you like to do?");
            System.out.println("1. Encode a new phrase");
            System.out.println("2. Decode a binary string");
            System.out.println("3. Exit");
            System.out.print("Enter option number: ");
            String option = scanner.nextLine().trim();

            if (option.equals("1")) {
                System.out.print("Enter a phrase to encode: ");
                String newPhrase = scanner.nextLine();
                processPhrase(newPhrase);
            } else if (option.equals("2")) {
                if (root == null || huffmanCodes.isEmpty()) {
                    System.out.println("[Error] No Huffman Tree exists. Please encode a phrase first.");
                    continue;
                }
                System.out.print("Enter a binary string to decode: ");
                String binary = scanner.nextLine();
                String decoded = decode(binary);
                System.out.println("Decoded Text: " + decoded);
            } else if (option.equals("3")) {
                System.out.println("Exiting program.");
                break;
            } else {
                System.out.println("Invalid option. Please enter 1, 2, or 3.");
            }
        }

        scanner.close();
    }

    // Handles encoding + display
    private static void processPhrase(String phrase) {
        if (phrase == null || phrase.trim().isEmpty()) {
            System.out.println("[Error] Cannot encode an empty phrase.");
            return;
        }

        buildHuffmanTree(phrase);

        System.out.println("\nGenerated Huffman Codes:");
        huffmanCodes.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> System.out.println("'" + entry.getKey() + "': " + entry.getValue()));

        String encoded = encode(phrase);
        System.out.println("\nEncoded Binary:");
        System.out.println(encoded);

        String decoded = decode(encoded);
        System.out.println("\nDecoded Text (from encoded binary):");
        System.out.println(decoded);
    }

    private static Map<Character, Integer> buildFrequency(String input) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char ch : input.toCharArray()) {
            freqMap.put(ch, freqMap.getOrDefault(ch, 0) + 1);
        }
        return freqMap;
    }

    private static void buildHuffmanTree(String input) {
        huffmanCodes.clear();
        Map<Character, Integer> freqMap = buildFrequency(input);
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(new HuffmanComparator());

        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        // Single-character edge case
        if (pq.size() == 1) {
            root = pq.poll();
            generateCodes(root, "");
            return;
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();

            HuffmanNode merged = new HuffmanNode('\0', left.freq + right.freq);
            merged.left = left;
            merged.right = right;

            pq.add(merged);
        }

        root = pq.poll();
        generateCodes(root, "");
    }

    private static void generateCodes(HuffmanNode node, String code) {
        if (node == null) return;

        if (node.isLeaf()) {
            huffmanCodes.put(node.ch, code.isEmpty() ? "0" : code);
        }

        generateCodes(node.left, code + "0");
        generateCodes(node.right, code + "1");
    }

    private static String encode(String text) {
        StringBuilder encoded = new StringBuilder();
        for (char ch : text.toCharArray()) {
            encoded.append(huffmanCodes.get(ch));
        }
        return encoded.toString();
    }

    private static String decode(String binary) {
        StringBuilder decoded = new StringBuilder();

        if (root == null) return "";

        if (!binary.matches("[01]+")) {
            return "[Error] Invalid binary input. Only 0s and 1s are allowed.";
        }

        // Single-character tree
        if (root.isLeaf()) {
            for (int i = 0; i < binary.length(); i++) {
                decoded.append(root.ch);
            }
            return decoded.toString();
        }

        HuffmanNode current = root;
        for (int i = 0; i < binary.length(); i++) {
            char bit = binary.charAt(i);
            current = (bit == '0') ? current.left : current.right;

            if (current == null) {
                return "[Error] Invalid binary path: does not match any Huffman code.";
            }

            if (current.isLeaf()) {
                decoded.append(current.ch);
                current = root;
            }
        }

        return decoded.toString();
    }
}
