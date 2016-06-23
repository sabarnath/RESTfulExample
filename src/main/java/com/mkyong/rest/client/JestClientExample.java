package com.mkyong.rest.client;



/**
**/
public class JestClientExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

    private static void indexSomeData(final JestClient jestClient) throws Exception {
        // Blocking index
        final Note note1 = new Note("mthomas", "Note1: do u see this - " + System.currentTimeMillis());
        Index index = new Index.Builder(note1).index(DIARY_INDEX_NAME).type(NOTES_TYPE_NAME).build();
        jestClient.execute(index);

        // Asynch index
        final Note note2 = new Note("mthomas", "Note2: do u see this - " + System.currentTimeMillis());
        index = new Index.Builder(note2).index(DIARY_INDEX_NAME).type(NOTES_TYPE_NAME).build();
        jestClient.executeAsync(index, new JestResultHandler() {
            public void failed(Exception ex) {
            }

            public void completed(JestResult result) {
                note2.setId((String) result.getValue("_id"));
                System.out.println("completed==&gt;&gt;" + note2);
            }
        });

        // bulk index
        final Note note3 = new Note("mthomas", "Note3: do u see this - " + System.currentTimeMillis());
        final Note note4 = new Note("mthomas", "Note4: do u see this - " + System.currentTimeMillis());
        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(note3).index(DIARY_INDEX_NAME).type(NOTES_TYPE_NAME).build())
                .addAction(new Index.Builder(note4).index(DIARY_INDEX_NAME).type(NOTES_TYPE_NAME).build())
                .build();
        JestResult result = jestClient.execute(bulk);

        Thread.sleep(2000);

        System.out.println(result.toString());
    }

    private static void readAllData(final JestClient jestClient) throws Exception {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("note", "see"));

        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(DIARY_INDEX_NAME)
                .addType(NOTES_TYPE_NAME).build();
        System.out.println(searchSourceBuilder.toString());
        JestResult result = jestClient.execute(search);
        List notes = result.getSourceAsObjectList(Note.class);
        for (Note note : notes) {
            System.out.println(note);
        }
    }
}
