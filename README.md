# Cose da migliorare

- scegliere un nome di package, e spostare le classi. Suggerimento: com.aton.m14e.yaml (e similari per le altre parti di progetto) 
- togliere i file YAML dai sorgenti, e metterli in una directory destinata ai dati da analizzare
- gestire come parametri directory dei sorgenti YAML e dell'output
- Gestire piu' metodi HTTP e casi OpenAPI non standard.
- Aggiungere gestione errori piu' robusta sui file malformati.

# Cose da fare nel notebook

- Aggiungere una sezione di quality check iniziale sui CSV (missing, tipi, righe duplicate).
- Inserire analisi top 10 nodi per inDegree, outDegree e total con breve commento.
- Confrontare la distribuzione dei tipi di dipendenza (`type`) con un grafico.
- Aggiungere una sezione dedicata ai nodi piu' critici e al loro impatto.
- Tracciare un mini piano di refactor basato sui risultati dell'analisi.
