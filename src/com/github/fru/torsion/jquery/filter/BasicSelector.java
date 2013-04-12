package com.github.fru.torsion.jquery.filter;

import java.util.Collection;

import org.w3c.dom.Node;

import com.github.fru.torsion.jquery.utils.AttributeUtilities;
import com.github.fru.torsion.jquery.utils.BasicUtilities;

class BasicSelector extends AbstractSelector {
	private CssToken token;

	private String key = null;
	private String value = null;
	private char sign = 0;

	protected BasicSelector(CssToken token) {
		this.token = token;
		if (token.type == '[') {
			int eq = token.content.indexOf('=');
			if (eq == -1) {
				key = token.content;
			} else {
				key = token.content.substring(0, eq);
				value = token.content.substring(eq + 1);
				if (key.length() == 0 || value.length() == 0) {
					key = null;
					return;
				}

				if (value.length() > 2 && value.charAt(0) == '\'' && value.charAt(value.length() - 1) == '\'') {
					value = value.substring(1, value.length() - 1);
				}

				sign = key.charAt(key.length() - 1);
				if (sign == '|' || sign == '*' || sign == '~' || sign == '$' || sign == '!' || sign == '^') {
					key = key.substring(0, key.length() - 1);
				} else {
					sign = 0;
				}
			}
		}
	}

	@Override
	protected Collection<Node> match(Collection<Node> nodes) {
		if (token.type == 't' && "*".equals(token.content)) return nodes;
		return super.match(nodes);
	}

	@Override
	protected boolean match(Node node) {
		if (token == null) return false;
		if (token.type == 't') {
			return token.content != null && token.content.equals(node.getNodeName());
		} else if (token.type == '#') {
			return token.content != null && token.content.equals(AttributeUtilities.getAttribute(node, "id"));
		} else if (token.type == '.') {
			return BasicUtilities.parseClasses(AttributeUtilities.getAttribute(node, "class")).contains(token.content);
		} else if (token.type == '[') {
			if (key == null) return false;
			if (value == null) return AttributeUtilities.hasAttribute(node, key);

			String attribute = AttributeUtilities.getAttribute(node, key);
			if (attribute == null) return false;

			// TODO: ~ not just matches spaces but all whitespace
			if (sign == '|')
				return attribute.startsWith(value + "-") || attribute.equals(value);
			else if (sign == '*')
				return attribute.contains(value);
			else if (sign == '~')
				return attribute.startsWith(value + " ") || attribute.endsWith(" " + value) || attribute.contains(" " + value + " ");
			else if (sign == '$')
				return attribute.endsWith(value);
			else if (sign == '!')
				return !attribute.equals(value);
			else
				return attribute.equals(value);
		} else {
			return false;
		}
	}

	@Override
	protected void make(Node node) {
		if (token.type == '#') {
			AttributeUtilities.setAttribute(node, "id", token.content);
		} else if (token.type == '.') {
			BasicUtilities.addClasses(node, token.content);
		} else if (token.type == '[') {
			AttributeUtilities.setAttribute(node, key, value != null ? value : "");
		}
	}
}