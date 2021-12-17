/**

DESCRIPTION:

The ipv4 address takes 4 bytes for the memory storage and easily can be converted to the Int32 type.
The array of integers with the size equal to UInt32.MAX_VALUE can be used to store counters for the ip addresses, but it will require to allocate approx. 16GB of memory.
A little bit less efficient access to the counters can be provided by using the HashMap. This solution allows decrease the memory usage significantly.
With the HashMap two types of keys can be used: the ip address converted to integer or the ip address string. In first case - it uses less memory (4 bytes for integer vs 8-16 bytes for the string),
and additional time for the ip address conversion from string to int. The access time in both cases will be similar.

Question:
What would you do differently if you had more time?
Answer:
I'd change the way the top100 list filled with values - instead of sorting the list after each modification - sort it once and then insert the Pair in the appropriate position returned by binarySearch method.
Also, I would change the ip conversion for use with ipv6 addresses too.

Question:
What is the runtime complexity of each function?
Answer:
request_handled - Time complexity: Θ(1); Space Complexity: O(n)
top100 - Time complexity: O(n); Space Complexity: O(n)
clear - Time complexity: O(n); Space Complexity: O(n)

Question:
How does your code work?
Answer:
The description of the counting process:
1. The string ip address converted to the integer "ip". The counter for the "ip" taken from the HashMap or initialized with 0. After that counter incremented by 1 and entry saved back to the HashMap.
2. If the "ip" already present in the top100 list - the entry counter will be increased by 1.
3. If the "ip" is not present and the size of top100 list is still less than 100 entries - the pair "ip" and counter will be added to this list
4. If the top100 list already has 100 entries in it - then the counter from the last entry of top100 list compared with current counter. If counter from the list is less than current counter the entry will be replaced with current "ip" and counter
5. The top100 array re-sorted after that in descending for the counters order, to make sure the last entry contains the lowest counter value.

Question:
What other approaches did you decide not to pursue?
Answer:
To use the Int32 array for the counters.

Question:
How would you test this?
Answer:
I'll use the ip address generator to simulate the ip addresses input, will use unit tests to test the functions, will use "measureTimeMillis" function to measure the execution
time for the critical pieces.


 */

fun main(){
	// Storage for the ip addresses
	val ipdata = HashMap<Int, Int>()
	// Storage for top 100 ipv4 addresses with counters
	val top = ArrayList<Pair<String, Int>>()

	val test = arrayOf("1.1.1.1", "127.0.0.1", "192.164.0.1", "8.8.8.8", "8.8.8.4", "8.8.8.8", "8.8.8.8", "8.8.8.8", "8.8.8.8", "8.8.8.8", "8.8.8.8", "8.8.8.8", "8.8.8.8", "8.8.8.4", "8.8.8.4", "8.8.8.4", "127.0.0.1", "255.255.255.255")
	for(i in 0 until test.size-1){
		request_handled(test[i])
	}
	println(top100().asList())
	clear()
	top100()

	fun request_handled(ip_address : String) {
		val bytes = ip_address.split(".")
		// Convert the ip address parts into bytes and sum all bytes together
		val ip = bytes[0].toInt() shl 24 or bytes[1].toInt() shl 16 or bytes[2].toInt() shl 8 or bytes[3].toInt()
		// Evaluate the counter for the ip
		val cnt = (ipdata[ip] ?: 0) + 1
		// Save the new counter value
		ipdata.put(ip, cnt)
		val item = Pair(ip_address, cnt)
		// Get the pos of this ip in the top array
		val pos = top.indexOfFirst { it -> it.first == ip_address }
		if(pos >= 0){
			// If ip present - update counter there
			top[pos] = item
		} else if(top.size < 100){
			// If ip is not present and number of the top ip addresses less than 100 - add it to that list
			top.add(item)
		} else if(top[top.lastIndex].second < cnt) {
			// Otherwise compare the counter of last item in top100 list with counter for current ip. If it less, replace it with current ip
			top[top.lastIndex] = item
		} else {
			return
		}
		// Sort top100 list by descending counters, i.e. last item has the lowest counter
		top.sortWith(compareByDescending { it.second })
	}

	fun top100() : Array<String> {
		// Filter pairs with non-zero counters and map it into ip addresses collection and to the string array
		return top.map { it -> it.first }.toTypedArray()
	}

	fun clear(){
		// Clear all collected data
		ipdata.clear()
		top.clear()
	}

}
