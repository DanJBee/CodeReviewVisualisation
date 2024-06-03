# Logger

import logging
import sys


# Initialises the logger
def init_logger(log_name: str):
    log_format = "%(asctime)s [%(levelname)-5.5s %(message)s"
    # Saves debug logs to a file named "execution.log"
    logging.basicConfig(filename=log_name, level=logging.DEBUG, format=log_format)
    formatter = logging.Formatter(log_format)
    stream_handler = logging.StreamHandler(sys.stdout)
    stream_handler.setFormatter(formatter)
    logging.getLogger().addHandler(stream_handler)
