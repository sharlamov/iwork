package com.report.work;

public class TLongIntItemsList {

	TLongIntItem First, Last, Current;
	long Count;
	boolean Eol;

	public TLongIntItemsList() {
		First = null;
		Last = null;
		Current = null;
		Count = 0;
		Eol = true;
	}

	// ---------------------------------------------------------------------------
	void Add(long pIndex) {
		if (First != null) {
			Last.Next = new TLongIntItem(pIndex);
			Last = Last.Next;
		} else {
			First = new TLongIntItem(pIndex);
			Last = First;
		}
		Count++;
		Eol = Current == null;
	}

	// ---------------------------------------------------------------------------
	void GoTop() {
		Current = First;
		Eol = Current == null;
	}

	// ---------------------------------------------------------------------------
	void Next() {
		if (Current != null)
			Current = Current.Next;
		Eol = Current == null;
	}

	// ---------------------------------------------------------------------------
	long CurrIndex() {
		if (Current != null)
			return Current.Index;
		else
			return -1;
	}

}
