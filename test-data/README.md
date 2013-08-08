The documentation ( https://en.bitcoin.it/wiki/Dump_format#CBlock ) says that 2d7f4d.dat concatenated with 3407a8.dat should double-SHA256 hash to 8ebc6a.

However, when I try this I get a different result.  This is what happens:

% cat should-hash-to-8ebc6a.dat| openssl dgst -sha256 -binary | openssl dgst -sha256
f840ae66eeb6336a576b7e0d543d47063c4d49ae9d04c89b8bba5c257f134652

The data for the blocks was retrieved from: http://blockexplorer.com/rawblock/00000000009ffdadbb2a8bcf8e8b1d68e1696802856c6a1d61561b1f630e79e7

Which is the raw data for block 72785 linked from here: http://blockexplorer.com/block/00000000009ffdadbb2a8bcf8e8b1d68e1696802856c6a1d61561b1f630e79e7

The Merkle tree values are listed in the JSON as:

"mrkl_tree":[
    "2d7f4d1c25893dcaf538fdd1f34104687211ca7d8a1ba43c16b618d5fbc620c3",
    "3407a84dce0fe04fdab91608d1974941af3683ea6e4d904a30469485c50d336a",
    "5edf5acf8f517d965219a5495321e0bedd761daf45bcdc59a33b07b520968b8c",
    "65c35615b476c86f28a4d3a8985ea161cc2e35e6574eacbd68942782ce29804c",
    "89aa32f6e1b047e740401ce4fd43a865631de5a959fde7451936c28c52249b56",
    "e3e69c802b7e36d220151e4ccdeace1d58ca2af97c5fd970314bbecd9767a514",
    "8ebc6ac1c5c656c19632f8b7efd130303a9710ed1c0ea12935255d6fefc5d3b4",
    "d5e41432e73312b7c82fe57303ff5bd3d0f82cb933a89bd5d80a11556fb54e07",
    "89b77c032617fb9c37f2f922264d87764f16307541880b569fc8ca52dcec074a",
    "d1074c2765e46d3102c51ff38d2f9a0eff87d90e5e70d89ce03f508bf5a65874",
    "70a4e6dfb21e1e341ba2893e87bcc5473d1884505d8556cd5866a6e369174786",
    "e81287dc0c00422aaf0db3e4586c48b01acd82b3108da6956cbd6baf19cfaf9a"
  ]

It is a bit easier to understand when annotated though:

"mrkl_tree":[
    "2d7f4d1c25893dcaf538fdd1f34104687211ca7d8a1ba43c16b618d5fbc620c3", level 3a
    "3407a84dce0fe04fdab91608d1974941af3683ea6e4d904a30469485c50d336a", level 3b
    "5edf5acf8f517d965219a5495321e0bedd761daf45bcdc59a33b07b520968b8c", level 3c
    "65c35615b476c86f28a4d3a8985ea161cc2e35e6574eacbd68942782ce29804c", level 3d
    "89aa32f6e1b047e740401ce4fd43a865631de5a959fde7451936c28c52249b56", level 3e
    "e3e69c802b7e36d220151e4ccdeace1d58ca2af97c5fd970314bbecd9767a514", level 3f
    "8ebc6ac1c5c656c19632f8b7efd130303a9710ed1c0ea12935255d6fefc5d3b4", level 2ab   (parent of ab)
    "d5e41432e73312b7c82fe57303ff5bd3d0f82cb933a89bd5d80a11556fb54e07", level 2cd   (parent of cd)
    "89b77c032617fb9c37f2f922264d87764f16307541880b569fc8ca52dcec074a", level 2ef   (parent of ef)
    "d1074c2765e46d3102c51ff38d2f9a0eff87d90e5e70d89ce03f508bf5a65874", level 1abcd (parent of abcd)
    "70a4e6dfb21e1e341ba2893e87bcc5473d1884505d8556cd5866a6e369174786", level 1efef (parent of ef, 2ef is repeated twice for this calculation)
    "e81287dc0c00422aaf0db3e4586c48b01acd82b3108da6956cbd6baf19cfaf9a", level 0     (the root)
  ]

And as a true tree (from the Wiki):

              ---------e81287-------
              |                    |
       ----d1074c-----          70a4e6 (this is a hash of two 89b77c hashes)
       |             |             |
   -8ebc6a-      -d5e414-      -89b77c-
   |      |      |      |      |      |
2d7f4d 3407a8 5edf5a 65c356 89aa32 e3e69c
