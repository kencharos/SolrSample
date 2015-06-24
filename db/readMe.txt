##  Backup to sql file
java -cp h2-1.4.185.jar  org.h2.tools.Script -url "jdbc:h2:file:~/solrDB" -user "sa" -script "./backup.sql"

## Restre from sql file
java -cp h2-1.4.185.jar  org.h2.tools.RunScript -url "jdbc:h2:file:~/solrDB" -user "sa" -script "./backup.sql"
