--
-- Table structure for table `keysvotes`
--

CREATE TABLE IF NOT EXISTS `keysvotes` (
  `idvotation` bigint(10) NOT NULL,
  `publicKey` varchar(2048) NOT NULL,
  `privateKey` varchar(2048) NOT NULL,
  PRIMARY KEY (`idvotation`),
  UNIQUE KEY `idvotation` (`idvotation`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;