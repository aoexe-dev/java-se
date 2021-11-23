package fundation.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * AC自动机
 * 
 * @author Administrator
 * @date 2021-11-01 21:12:42
 */
public class AhoCorasick {

	private TrieNode root;

	public AhoCorasick() {
		// TODO Auto-generated constructor stub
		root = new TrieNode();
	}

	private void buildTrie(Collection<String> pats) {
		for (String pat : pats) {
			TrieNode node = this.root;
			int depth = 0;
			for (char ch : pat.toCharArray()) {
				if (!node.children.containsKey(ch)) {
					node.children.put(ch, new TrieNode());
				}
				node = node.children.get(ch);
				node.depth = depth++;
			}
			node.isEnd = true;
		}
	}

	private void buildFail() {
		LinkedList<TrieNode> list = new LinkedList<>();
		list.offer(this.root);
		while (!list.isEmpty()) {
			TrieNode node = list.poll();
			node.children.entrySet().forEach(e -> {
				TrieNode fail = node.fail;
				TrieNode child = e.getValue();
				while (fail != null) {
					if (fail.children.containsKey(e.getKey())) {
						child.fail = fail.children.get(e.getKey());
					}
					fail = fail.fail;
				}
				if (fail == null) {
					child.fail = root;
				}
				list.add(child);
			});
		}
	}

	public Map<String, List<Integer>> match(String text) {
		Map<String, List<Integer>> result = new HashMap<>();
		TrieNode node = this.root;
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			while (!node.children.containsKey(ch) && node != root) {
				node = node.fail;
			}
			node = node.children.get(ch);
			if (node == null) {
				node = root;
			}
			TrieNode temp = node;
			while (temp != null) {
				if (temp.isEnd) {
					String key = text.substring(i - temp.depth, i + 1);
					if (!result.containsKey(key)) {
						result.put(key, new LinkedList<>());
					}
					result.get(key).add(i - temp.depth);
				}
				temp = temp.fail;
			}
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		File pat = new File(args[0]);
		File text = new File(args[1]);
		List<String> list = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(pat));
		String str = null;
		while ((str = br.readLine()) != null) {
			list.add(str);
		}
		br.close();
		AhoCorasick ac = new AhoCorasick();
		ac.buildTrie(list);
		ac.buildFail();
		br = new BufferedReader(new FileReader(text));
		StringBuilder sb = new StringBuilder();
		while ((str = br.readLine()) != null) {
			sb.append(str);
		}
		br.close();
		long start = System.currentTimeMillis();
		Map<String, List<Integer>> match = ac.match(sb.toString());
		long end = System.currentTimeMillis();
		match.entrySet().forEach(e -> {
			System.out.println(e.getKey() + ":" + e.getValue());
		});
		System.out.println(end - start);
		
	}
}