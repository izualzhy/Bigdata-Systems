#!/usr/bin/env python
# coding=utf-8
from collections import defaultdict
from xml.etree import ElementTree


class CapacitySchedulerXMLParser(object):
    def __init__(self, xml_path):
        self._xml_path = xml_path
        self._xml_tree = ElementTree.ElementTree(file=self._xml_path)
        self._queues = defaultdict(dict)

    def parse(self):
        xml_root = self._xml_tree.getroot()

        property_nodes = xml_root.findall('.//property')
        for property_node in property_nodes:
            name_node = property_node.find('name')
            value_node = property_node.find('value')

            if name_node is not None and value_node is not None:
                name = name_node.text
                value = value_node.text

                if name.startswith("yarn.scheduler.capacity.root.") and value:
                    queue_name = name.split(".")[-2]
                    attribute = name.split(".")[-1]
                    self._queues[queue_name][attribute] = value

    def dump(self):
        for queue in self._queues:
            if self._queues[queue].get('state') == 'RUNNING':
                print(queue)
                print(self._queues[queue])


if __name__ == '__main__':
    capacity_scheduler_xml_parser = CapacitySchedulerXMLParser('/tmp/capacity-scheduler.xml')
    capacity_scheduler_xml_parser.parse()
    capacity_scheduler_xml_parser.dump()
