package TextEditor;

import java.util.Objects;

public class Word implements Comparable <Word>{ 
	String word;
	
	Word(String word) {
		this.word = word;
	}
	
	@Override
	public int hashCode() {  
	return Objects.hash(word);   //hash on instance variable values
	}
	
	@Override
	public boolean equals(Object o) { 
		if (o == null) return false;  // if o is null, then it can’t be equal to ‘this’ 
		if (this == o) return true;   // if both point to same object, then they are equal
		if (getClass() != o.getClass()) return false; //if not of same type, they can’t be equal
		Word b = (Word) o; // cast none word object
		return word.equals(b.word); //compare values
	}

	@Override
	public int compareTo(Word o) {
		return this.word.compareTo(o.word);
	}
}
